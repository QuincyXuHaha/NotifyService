package com.jufan.main.initializer;

import com.jufan.main.factory.consumer.ResendConsumer;
import com.jufan.main.factory.consumer.UpdateConsumer;
import com.jufan.main.factory.depot.ResendDepot;
import com.jufan.main.factory.depot.UpdateDepot;
import com.jufan.main.runner.RunnablePuller;
import com.jufan.main.runner.RunnableRedisSynchronizer;
import com.jufan.model.MessageModel;
import com.jufan.service.cache.MessageRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * 应用初始化
 *
 * @author 李尧
 * @since  0.1.0
 */
@Component
public class ApplicationInitializer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResendDepot resendDepot;

    @Autowired
    private UpdateDepot updateDepot;

    @Autowired
    private MessageRedisService messageRedisService;

    @Autowired
    private RunnablePuller runnablePuller;

    @Autowired
    private ResendConsumer resendConsumer;

    @Autowired
    private RunnableRedisSynchronizer runnableRedisSynchronizer;

    @Autowired
    private UpdateConsumer updateConsumer;

    public void init() {
        // 从redis同步depot
        synchronizeToUpdateDepot();
        synchronizeToResendDepot();

        // 启动核心线程

        // Puller
        new Thread(runnablePuller).start();

        // 重复发送
        new Thread(resendConsumer).start();

        // DepotSynchronizer
        new Thread(runnableRedisSynchronizer).start();

        // 持久化
        new Thread(updateConsumer).start();

    }

    private void synchronizeToResendDepot() {

        Set<Object> hashKeys = messageRedisService.hashKeys(ResendDepot.REDISKEY_DEPOT);

        ConcurrentHashMap<String, MessageModel> messages = new ConcurrentHashMap<>();
        for (Object hashKey : hashKeys) {
            MessageModel message = messageRedisService.get(ResendDepot.REDISKEY_DEPOT, (String) hashKey);
            if (messages.put(message.getId(), message) != null)
                logger.warn("ApplicationInitializer -> synchronizeResendDepot -> Message repeated! id = " + message.getId());
        }

        resendDepot.recoverMessages(messages);
    }

    private void synchronizeToUpdateDepot() {

        Set<Object> hashKeys = messageRedisService.hashKeys(UpdateDepot.REDISKEY_DEPOT);

        ConcurrentLinkedQueue<MessageModel> messages = new ConcurrentLinkedQueue<>();
        for (Object hashKey : hashKeys) {
            MessageModel message = messageRedisService.get(UpdateDepot.REDISKEY_DEPOT, (String) hashKey);
            messages.offer(message);
        }

        updateDepot.recoverMessages(messages);
    }
}
