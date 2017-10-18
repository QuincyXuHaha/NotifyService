package com.jufan.main.factory.producer;

import com.jufan.main.factory.depot.UpdateDepot;
import com.jufan.model.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * {@link UpdateDepot}的生产者
 *
 * 主要是{@link com.jufan.main.factory.depot.ResendDepot}的重发过程中产生的更新会通过此类存入{@link UpdateDepot}中
 *
 * @see UpdateDepot
 *
 * @author 李尧
 * @since  0.1.0
 */
@Component
public class UpdateProducer {

    @Autowired
    private UpdateDepot updateDepot;

    // 为了保证顺序，使用单线程的线程池
    private ExecutorService producePool = Executors.newSingleThreadExecutor();

    public void push(MessageModel message) {
        producePool.execute(new Pusher(updateDepot, message));
    }

}

class Pusher implements Runnable {

    private UpdateDepot depot;

    private MessageModel message;

    Pusher(UpdateDepot depot, MessageModel message) {
        this.depot = depot;
        this.message = message;
    }

    @Override
    public void run() {
        depot.produce(message);
    }
}
