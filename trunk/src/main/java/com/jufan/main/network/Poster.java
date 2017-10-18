package com.jufan.main.network;

import com.jufan.dao.repository.NetLogRepo;
import com.jufan.main.factory.depot.ResendDepot;
import com.jufan.main.runner.RunnableLogger;
import com.jufan.main.runner.RunnableSendingStopper;
import com.jufan.entity.NetLogEntity;
import com.jufan.model.MessageModel;
import com.jufan.model.SSLResponse;
import com.jufan.util.HttpUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 李尧
 * date: 2017/8/18
 */
@Component
@Scope("prototype")
public class Poster implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Poster.class);

    @Autowired
    private Bootstrap bootstrap;

    @Autowired
    private NetLogRepo netLogRepo;

    @Autowired
    private ResendDepot resendDepot;

    @Autowired
    private AtomicInteger httpSendCount;

    private MessageModel message;

    private ExecutorService stopperPool = Executors.newSingleThreadExecutor();

    private ExecutorService loggerPool = Executors.newSingleThreadExecutor();

    @Override
    public void run() {

        try {
            if (isHttps(message.getTarget()))
                sslRequest();
            else
                nettyRequest();
        } catch (Exception e) {
            logger.error("Poster: (" + message.getSendLevel() + ")" + message.getId() + " send error. ", e);
        }
    }

    // 判断是否为https请求
    private boolean isHttps(String target) {
        return target.toLowerCase().startsWith("https");
    }

    // netty发送http请求
    private void nettyRequest() throws Exception {
        String id = message.getId();
        String sendLevel = message.getSendLevel();

        String host = HttpUtil.parseHost(message.getTarget());
        int port = HttpUtil.parsePort(message.getTarget());

        ChannelFuture future = bootstrap.connect(host, port);
        FullHttpRequest request = packagePacket(host/* + ":" + port*/);
        AttributeKey<String> attr = AttributeKey.valueOf("msgSign");
        future.channel().attr(attr).set(sendLevel + "," + id);
        future.get();
        future.channel().writeAndFlush(request);

        // 记录http报文日志
        String[] strs = request.toString().split("\r\n");
        StringBuilder packet = new StringBuilder();
        for (int i = 1; i < strs.length; i++)
            packet.append(strs[i]).append("\r\n");
        packet.append("\r\n").append(message.getContent());
        NetLogEntity netLog = NetLogEntity.newRequestEntity(id, sendLevel, packet.toString());
        loggerPool.execute(new RunnableLogger(netLogRepo, netLog));
        // 统计计数
        httpSendCount.getAndIncrement();
    }

    // httpClient发送https请求
    private void sslRequest() throws IOException {
        String id = message.getId();
        String sendLevel = message.getSendLevel();

        SSLResponse response = null;

        try {
            if ("GET".equals(message.getSendMethod().toUpperCase()))
                response = HttpUtil.sslGet(message.getTarget());
            if ("POST".equals(message.getSendMethod().toUpperCase()))
                response = HttpUtil.sslPost(message.getTarget(), message.getContent(), message.getContentType());
        } catch (Exception e) {
            logger.error("Poster: (" + sendLevel + ") " + id + " send error. ", e);
            return;
        }

        if (response == null) {
            logger.error("Poster: (" + sendLevel + ")" + id + " sendMethod error: " + message.getSendMethod());
            return;
        }

        // log
        NetLogEntity netLog = NetLogEntity.newRequestEntity(id, sendLevel, response.getReqPacket());
        loggerPool.execute(new RunnableLogger(netLogRepo, netLog));

        if (response.getResponse() == null) {
            logger.error("Poster: (" + sendLevel + ")" + id + " sendMethod error: " + message.getSendMethod());
            return;
        }

        String content = EntityUtils.toString(response.getResponse().getEntity());
        if (content.toUpperCase().contains("SUCCESS") || content.toUpperCase().contains("FAIL"))
            stopperPool.execute(new RunnableSendingStopper(resendDepot, id));

        netLog = NetLogEntity.newResponseEntity(id, sendLevel, HttpUtil.parseResponse(response.getResponse()));
        loggerPool.execute(new RunnableLogger(netLogRepo, netLog));
    }

    // 打包请求报文
    private FullHttpRequest packagePacket(String host) throws Exception {

        URI uri = new URI(HttpUtil.parsePath(message.getTarget()));
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.valueOf(message.getSendMethod().toUpperCase()),
                uri.toASCIIString(),
                Unpooled.wrappedBuffer(message.getContent().getBytes("UTF-8")));

        request.headers().set(HttpHeaderNames.HOST, host);
        switch (message.getContentType()) {
            case 1:
                request.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED);
                break;
            case 2:
                request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
                break;
        }
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
        request.headers().set(HttpHeaderNames.DATE, new Date());
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        return request;
    }

    public Poster setMessage(MessageModel message) {
        this.message = message;
        return this;
    }

}
