package com.jufan.config;

import com.jufan.main.network.HttpClientInboundHandler;
import com.jufan.entity.MessageEntity;
import com.jufan.service.cache.impl.RedisObjectSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 自定义的Beans
 *
 * @see com.jufan.MessageSysApplication
 *
 * @author 李尧
 * @since  0.1.0
 */
@Configuration
public class CustomBeans {

    @Bean
    public RedisTemplate<String, MessageEntity> redisTemplate(RedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, MessageEntity> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public Bootstrap messageBootstrap(HttpClientInboundHandler httpClientInboundHandler) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("decoder", new HttpResponseDecoder());
                // 整合response
                ch.pipeline().addLast("aggregator", new HttpObjectAggregator(131072));
                ch.pipeline().addLast("encoder", new HttpRequestEncoder());
                ch.pipeline().addLast("handler", httpClientInboundHandler);
            }
        });
        return bootstrap;
    }

    // 计数君们
    @Bean
    public AtomicInteger firstCount() {
        return new AtomicInteger(0);
    }
    @Bean
    public AtomicInteger sendCount() {
        return new AtomicInteger(0);
    }
    @Bean
    public AtomicInteger finishedCount() {
        return new AtomicInteger(0);
    }
    @Bean
    public AtomicInteger stoppedCount() {
        return new AtomicInteger(0);
    }
    @Bean
    public AtomicInteger httpSendCount() {
        return new AtomicInteger(0);
    }
    @Bean
    public AtomicInteger updatedCount() {
        return new AtomicInteger(0);
    }

}
