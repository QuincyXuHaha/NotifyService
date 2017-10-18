package com.jufan.service.cache.impl;

import com.jufan.entity.MessageEntity;
import com.jufan.model.MessageModel;
import com.jufan.service.cache.MessageRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

/**
 *
 * 提供基本Redis操作的一个Service的实现而已
 *
 * @author 李尧
 * @since  0.1.0
 */
@Service
public class MessageRedisServiceImpl implements MessageRedisService {

    @Autowired
    private RedisTemplate<String, MessageEntity> messageRedisTemplate;

    @Override
    public void put(String key, MessageModel message) {
        messageRedisTemplate.opsForHash().put(key, message.getId(), message);
    }

    @Override
    public MessageModel get(String key, String hashKey) {
        return (MessageModel) messageRedisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public void delete(String key, String hashKey) {
        messageRedisTemplate.opsForHash().delete(key, hashKey);
    }

    @Override
    public Set<Object> hashKeys(String key) {
        return messageRedisTemplate.opsForHash().keys(key);
    }

}
