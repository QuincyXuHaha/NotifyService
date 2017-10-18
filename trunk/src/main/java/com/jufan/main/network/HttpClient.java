package com.jufan.main.network;

import com.jufan.model.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 基于Netty实现的HttpClient
 *
 * @author 李尧
 * @since  0.2.0
 */
@Component
public class HttpClient {

    private final ApplicationContext context;

    // 给整个系统最慢的地方分配尽量多的资源
    private ExecutorService senderPool = Executors.newFixedThreadPool(16);

    @Autowired
    public HttpClient(ApplicationContext context) {
        this.context = context;
    }

    // 将message交由Poster发送
    public void post(MessageModel message) throws Exception {
        senderPool.execute(context.getBean("poster", Poster.class).setMessage(message));
    }

}
