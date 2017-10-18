package com.jufan.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 李尧
 * @since  0.2.0
 */
public class BeanUtil {

    private static final Logger LOGGER = Logger.getLogger(BeanUtil.class);

    /**
     * Bean -> Map
     */
    public static Map<String, Object> bean2Map(Object bean) {

        return JSON.parseObject(
                JSON.toJSONString(bean),
                new TypeReference<Map<String, Object>>() {});

    }

    /**
     *
     * 实体类同名属性互传
     *
     * @param source 源实体
     * @param target 目标实体
     */
    public static void attrTransfer(Object source, Object target) {

        Class sClass = source.getClass();
        Class tClass = target.getClass();

        List<Field> sFields = Arrays.asList(sClass.getDeclaredFields());

        for (Field sField : sFields) {

            if ("serialVersionUID".equals(sField.getName()))
                continue;

            try {
                Field tField = tClass.getDeclaredField(sField.getName());
                sField.setAccessible(true);
                tField.setAccessible(true);
                tField.set(target, sField.get(source));
            } catch (NoSuchFieldException ignored) {
            } catch (IllegalAccessException e) {
                LOGGER.info("", e);
            }
        }
    }

}