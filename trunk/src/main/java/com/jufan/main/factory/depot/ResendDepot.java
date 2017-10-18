package com.jufan.main.factory.depot;

import com.jufan.main.factory.producer.UpdateProducer;
import com.jufan.main.network.HttpClient;
import com.jufan.main.runner.RunnableSendingStopper;
import com.jufan.model.MessageModel;
import com.jufan.monitor.ResendDepotMonitor;
import com.jufan.util.ResendCheckState;
import com.jufan.util.ResendUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * ResendDepot 消息重发仓库
 *
 * 应用初始化后，数据库中已处于可发送状态的Message将由{@link com.jufan.main.runner.RunnablePuller}不断获取（并改为发送中状态）并存入此仓库
 *
 * {@link com.jufan.main.factory.consumer.ResendConsumer}会不断循环遍历集合，并将Message交由{@link HttpClient}发送
 *
 * 以及将首次发送的Message交由{@link UpdateProducer}存入{@link UpdateDepot}
 *
 * <线程安全>
 *
 * @author 李尧
 * @since  0.1.0
 */
@Component
public class ResendDepot {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String REDISKEY_DEPOT = "NotifyService:ResendDepot";

    @Autowired
    private UpdateProducer updateProducer;

    @Autowired
    private HttpClient httpClient;

    /**
     * 仓库本体
     *
     * 虽然干的最多的事情是遍历，不过频繁的remove操作才是真正的效率杀手
     *
     * 因此选择{@link ConcurrentHashMap}
     */
    private ConcurrentHashMap<String, MessageModel> messages = new ConcurrentHashMap<>();

    // 首次发送计数
    @Autowired
    private AtomicInteger firstCount;

    // 重复发送计数
    @Autowired
    private AtomicInteger sendCount;

    // 发送完毕计数
    @Autowired
    private AtomicInteger finishedCount;

    // 发送终止计数
    @Autowired
    private AtomicInteger stoppedCount;

    // http请求计数
    @Autowired
    private AtomicInteger httpSendCount;

    /**
     * 在应用初始化执行时, 将会把Redis中的集合缓存同步至此，以作故障恢复之用
     */
    public void recoverMessages(ConcurrentHashMap<String, MessageModel> messages) {
        this.messages = messages;
    }

    /**
     * {@link com.jufan.main.runner.RunnableRedisSynchronizer}会将集合同步至Redis
     */
    public ConcurrentHashMap<String, MessageModel> getMessages() {
        return this.messages;
    }

    /**
     * 应用初始化后，{@link com.jufan.main.runner.RunnablePuller}将不断通过此方法将数据库中可发送的消息存入仓库
     *
     * 当库存大于50000时，此过程会阻塞1秒
     *
     * @param messagesIn 待存入的Message, 因为对于此数据的操作基本只是遍历和移除，因此选择队列比较节省效率
     * @throws InterruptedException 如果存入过程中发生异常，此过程将会重新启动
     */
    public void produce(LinkedList<MessageModel> messagesIn) throws InterruptedException {

        while (messagesIn == null || messagesIn.size() < 1 || messages.size() > 50000)
            Thread.sleep(1000);

        while (!messagesIn.isEmpty()) {
            MessageModel temp = messagesIn.poll();
            messages.put(temp.getId(), temp);
        }
    }

    /**
     * 应用初始化后，{@link com.jufan.main.factory.consumer.ResendConsumer}将不断执行此方法，判断仓库中每一条Message的重发状态并进行相应的处理
     *
     * FIRSTSEND: 首次发送。由于首次发送和后续重发的区别在于:
     *      首次发送的消息没有lastSendTime,须根据firstSendTime判断是否达到发送状态；
     *      因此需与SEND状态做区分。
     *
     * SEND: 重新发送。发送消息，并更改消息的状态。
     *
     * FINISHED: 重发完毕。将消息移出仓库并更改状态。
     *
     * WAIT: 继续等待。
     *
     * @throws InterruptedException 如果执行过程中发生异常，此过程将会重新启动
     */
    public void consume() throws Exception {

        while (messages.size() < 1)
            Thread.sleep(50);

        for (Map.Entry<String, MessageModel> entry : messages.entrySet()) {

            String id = entry.getKey();
            MessageModel message = entry.getValue();

            if (!message.valid()) {
                logger.warn("Resending: MessageModel valid failed, id = " + id);
                messages.remove(id);
                continue;
            }

            ResendCheckState checkState = ResendUtil.checkState(message);

            switch (checkState) {
                case FIRSTSEND:
                    send(message, firstCount);
                    break;
                case SEND:
                    send(message, sendCount);
                    break;
                case FINISHED:
                    finished(id, message);
                    break;
            }
        }

    }

    private void send(MessageModel message, AtomicInteger count) throws Exception {
        message.setSendLevel(message.getNowLevel());
        message.setLastSendTime(new Date());
        ResendUtil.levelUp(message);
        // 发送
        httpClient.post(message);
        // 送入数据库更新集合
        updateProducer.push(message);
        count.getAndIncrement();
    }

    private void finished(String id, MessageModel message) {
        if (messages.remove(id, message)) {
            message.setStatus(2);
            updateProducer.push(message);
            finishedCount.getAndIncrement();
        }
    }

    /**
     * 当收到http的回调请求时，{@link RunnableSendingStopper}将通过此方法停止消息的重发
     *
     * 在请求发出-收到回调这一过程中，可能存在消息已经重发完毕的情况
     *
     * @param id Message.id
     */
    public void stopResending(String id) {

        MessageModel message = messages.get(id);

        if (messages.remove(id, message)) {
            message.setStatus(0);
            updateProducer.push(message);
            stoppedCount.getAndIncrement();
        }
    }

    /**
     * 将需要监控的信息交给{@link ResendDepotMonitor}
     *
     * @param monitor 监视器
     */
    public void refreshInfo(ResendDepotMonitor monitor) {
        monitor.setDepotStock(messages.size());
        monitor.setFirstSendCount(firstCount.intValue());
        monitor.setSendCount(sendCount.intValue());
        monitor.setFinishedCount(finishedCount.intValue());
        monitor.setStoppedCount(stoppedCount.intValue());
        monitor.setHttpSendCount(httpSendCount.intValue());
    }

}
