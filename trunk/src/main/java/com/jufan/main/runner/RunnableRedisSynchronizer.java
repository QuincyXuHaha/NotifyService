package com.jufan.main.runner;

import com.jufan.main.factory.depot.ResendDepot;
import com.jufan.main.factory.depot.UpdateDepot;
import com.jufan.main.factory.switches.Switches;
import com.jufan.model.MessageModel;
import com.jufan.service.cache.MessageRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * Depot -> Redis同步器
 *
 * 循环同步{@link ResendDepot}和{@link UpdateDepot}至Redis。
 *
 * @author 李尧
 * @since  0.1.0
 */
@Component
public class RunnableRedisSynchronizer implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResendDepot resendDepot;

    @Autowired
    private UpdateDepot updateDepot;

    @Autowired
    private MessageRedisService messageRedisService;

    private ExecutorService sychronizerPool = Executors.newCachedThreadPool();

    private int crashedCount = 0;

    @Override
    public void run() {

        try {

            while (true) {

                Future resendFuture = sychronizerPool.submit(new ResendSynchronizer(messageRedisService, resendDepot));

                Future updateFuture = sychronizerPool.submit(new UpdateSynchronizer(messageRedisService, updateDepot));

                // 用Future控制一下速度，免得把Redis跑爆了
                resendFuture.get();
                updateFuture.get();
            }
        } catch (Exception e) {
            if (crashedCount >= 5) {
                Switches.shutdown(e);
                return;
            }
            logger.error("RunnableRedisSynchronizer crashed! restart.", e);
            crashedCount++;
            run();
        }
    }

}

class ResendSynchronizer implements Runnable {

    private MessageRedisService messageRedisService;

    private ResendDepot resendDepot;

    ResendSynchronizer(MessageRedisService messageRedisService, ResendDepot resendDepot) {
        this.messageRedisService = messageRedisService;
        this.resendDepot = resendDepot;
    }

    @Override
    public void run() {

        Set<Object> hashKeys = messageRedisService.hashKeys(ResendDepot.REDISKEY_DEPOT);
        Set<Object> localHashKeys = new HashSet<>();

        for (Map.Entry<String, MessageModel> entry : resendDepot.getMessages().entrySet()) {
            messageRedisService.put(ResendDepot.REDISKEY_DEPOT, entry.getValue());
            localHashKeys.add(entry.getKey());
        }

        for (Object hashKey : hashKeys) {
            if (!localHashKeys.contains(hashKey))
                messageRedisService.delete(ResendDepot.REDISKEY_DEPOT, (String) hashKey);
        }
    }
}

class UpdateSynchronizer implements Runnable {

    private MessageRedisService messageRedisService;

    private UpdateDepot updateDepot;

    UpdateSynchronizer(MessageRedisService messageRedisService, UpdateDepot updateDepot) {
        this.messageRedisService = messageRedisService;
        this.updateDepot = updateDepot;
    }

    @Override
    public void run() {

        Set<Object> hashKeys = messageRedisService.hashKeys(UpdateDepot.REDISKEY_DEPOT);
        Set<Object> localHashKeys = new HashSet<>();

        for (MessageModel message : updateDepot.getSendingMessages()) {
            messageRedisService.put(UpdateDepot.REDISKEY_DEPOT, message);
            localHashKeys.add(message.getId());
        }

        for (Object hashKey : hashKeys) {
            if (!localHashKeys.contains(hashKey))
                messageRedisService.delete(UpdateDepot.REDISKEY_DEPOT, (String) hashKey);
        }

    }
}