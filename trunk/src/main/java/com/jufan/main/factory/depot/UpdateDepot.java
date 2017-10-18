package com.jufan.main.factory.depot;

import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.jufan.dao.repository.MessageRepo;
import com.jufan.model.MessageModel;
import com.jufan.monitor.UpdateDepotMonitor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * UpdateDepot I/O队列缓存仓库
 *
 * 在发送过程中产生的消息状态改变均会存入此队列
 * 然后由{@link com.jufan.main.factory.consumer.UpdateConsumer}按照FIFO的规则执行更新。
 *
 * <线程安全>
 *
 * @author 李尧
 * @since  0.1.0
 */
@Component
public class UpdateDepot {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String REDISKEY_DEPOT = "NotifyService:UpdateDepot";

    @Autowired
    private MessageRepo messageRepo;

    /**
     * 仓库本体
     *
     * 使用{@link ConcurrentLinkedQueue}是为了实现FIFO的规则
     *
     */
    private ConcurrentLinkedQueue<MessageModel> messages = new ConcurrentLinkedQueue<>();

    /**
     * 处于发送生命周期中的消息
     *
     * 当{@link #produce(MessageModel)}将消息存入{@link #messages}时，会同时存入此HashSet。
     * 出于以下两个原因：
     * 1. 当同一条消息的多次状态更新请求要进入队列时，如果此条消息已存在于更新队列，则不重复加入队列。因为后续消息状态的改变会作用至尚未被移出队列的对象。
     *    不加入队列就可以避免多余的数据库操作，事实上反而可以加速；
     * 2. 基于上面一条原因，每次{@link #produce(MessageModel)}插入数据时都要进行contains的判断，而相比{@link ConcurrentLinkedQueue},
     *    {@link ConcurrentHashSet}(基于{@link ConcurrentHashMap})的contains操作效率要爆炸得多。
     *
     * 当{@link Updater}的update操作执行完毕后，消息将从集合中移出
     *
     * 综上，{@link #messages}用于方便实现数据库更新的FIFO规则，而此集合用于优化更新队列的运行逻辑。
     *
     */
    private ConcurrentHashSet<MessageModel> sendingMessages = new ConcurrentHashSet<>();

    // 更新操作线程池
    private ExecutorService updaterPool = Executors.newSingleThreadExecutor();

    // 更新操作数
    @Resource(name = "updatedCount")
    private AtomicInteger updatedCount;


    /**
     * 通过此方法将Message添加至仓库
     */
    public void produce(MessageModel message) {

        if (!sendingMessages.contains(message)) {
            sendingMessages.add(message);
            messages.offer(message);
        }
    }

    /**
     * {@link com.jufan.main.factory.consumer.UpdateConsumer}将从应用初始化后开始循环执行此方法
     *
     * @throws InterruptedException 当抛出异常时，此过程会再次启动
     */
    public void consume() throws InterruptedException {

        while (messages.size() < 1)
            Thread.sleep(100);

        MessageModel message = messages.poll();

        updaterPool.execute(new Updater(messageRepo, message, updatedCount, sendingMessages));
    }

    // 在应用初始化执行时, 将会把Redis中的集合缓存同步至此，以作故障恢复之用
    public void recoverMessages(ConcurrentLinkedQueue<MessageModel> sendingMessage) {
        this.messages = sendingMessage;
    }

    /**
     * {@link com.jufan.main.runner.RunnableRedisSynchronizer}会将集合同步至Redis
     */
    public ConcurrentHashSet<MessageModel> getSendingMessages() {
        return this.sendingMessages;
    }

    /**
     * 将需要监控的信息交给{@link UpdateDepotMonitor}
     *
     * @param monitor 监视器
     */
    public void refreshInfo(UpdateDepotMonitor monitor) {
        monitor.setDepotStock(messages.size());
        monitor.setUpdateCount(updatedCount.intValue());
    }

}

class Updater implements Runnable {

    private MessageRepo messageRepo;

    private MessageModel message;

    private AtomicInteger updatedCount;

    private ConcurrentHashSet<MessageModel> sendingMessages;

    Updater(MessageRepo messageRepo, MessageModel message, AtomicInteger updatedCount, ConcurrentHashSet<MessageModel> sendingMessages) {
        this.messageRepo = messageRepo;
        this.message = message;
        this.updatedCount = updatedCount;
        this.sendingMessages = sendingMessages;
    }

    @Override
    public void run() {

        sendingMessages.remove(message);
        MessageModel snapShot = message.clone();

        snapShot.setUpdateTime(new Date());
        messageRepo.updateMessage(
                snapShot.getId(),
                snapShot.getUpdateTime(),
                snapShot.getStatus(),
                snapShot.getNowLevel(),
                snapShot.getLastSendTime());

        updatedCount.getAndIncrement();
    }
}