package com.jufan.service.cache;

import com.jufan.model.MessageModel;

import java.util.Set;

/**
 *
 * 一个Service
 *
 * @author 李尧
 * @since  0.1.0
 */
public interface MessageRedisService {

    void put(String key, MessageModel message);

    MessageModel get(String key, String hashKey);

    void delete(String key, String hashKey);

    Set<Object> hashKeys(String key);
}
