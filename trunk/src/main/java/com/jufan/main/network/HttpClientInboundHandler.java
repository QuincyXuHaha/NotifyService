package com.jufan.main.network;

import com.jufan.dao.repository.NetLogRepo;
import com.jufan.main.factory.depot.ResendDepot;
import com.jufan.main.runner.RunnableLogger;
import com.jufan.main.runner.RunnableSendingStopper;
import com.jufan.entity.NetLogEntity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Http入界处理
 *
 * @author 李尧
 * @since  0.2.0
 */
@Component
@ChannelHandler.Sharable
public class HttpClientInboundHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private ResendDepot resendDepot;

    @Autowired
    private NetLogRepo netLogRepo;

    private ExecutorService stopperPool = Executors.newFixedThreadPool(4);

    private ExecutorService loggerPool = Executors.newFixedThreadPool(4);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpResponse) {

            String msgSign = (String) ctx.channel().attr(AttributeKey.valueOf("msgSign")).get();

            // Stop resend
            FullHttpResponse response = (FullHttpResponse) msg;
            ByteBuf buf = response.content();
            String content = buf.toString(CharsetUtil.UTF_8);
            String uc = content.toUpperCase();
            if (uc.contains("SUCCESS") || uc.contains("FAIL"))
                stopperPool.execute(new RunnableSendingStopper(resendDepot, msgSign.split(",")[1]));

            // Record log
            String[] temp = msgSign.split(",");
            String packet = response.toString() + "\r\n\r\n" + response.content().toString(CharsetUtil.UTF_8);
            NetLogEntity netLog = NetLogEntity.newResponseEntity(temp[1], temp[0], packet);
            loggerPool.execute(new RunnableLogger(netLogRepo, netLog));

            buf.release();
        }

        ctx.channel().close();
    }
}
