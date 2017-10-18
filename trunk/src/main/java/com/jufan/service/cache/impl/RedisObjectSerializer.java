package com.jufan.service.cache.impl;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 *
 * 注册RedisTemplate的Bean时需要指定解码/编码器
 *
 * @see com.jufan.config.CustomBeans
 *
 * @author 李尧
 * date: 2017-8-23
 */
public class RedisObjectSerializer implements RedisSerializer<Object> {

    private Converter<Object, byte[]> serializer = new SerializingConverter();
    private Converter<byte[], Object> deserializer = new DeserializingConverter();

    private static final byte[] EMPTY_ARRAY = new byte[0];

    @Override
    public byte[] serialize(Object o) {

        if (o == null)
            return EMPTY_ARRAY;

        return serializer.convert(o);
    }

    @Override
    public Object deserialize(byte[] bytes) {

        if (bytes == null || bytes.length < 1)
            return null;

        return deserializer.convert(bytes);
    }
}
