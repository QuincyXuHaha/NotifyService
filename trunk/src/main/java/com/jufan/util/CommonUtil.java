package com.jufan.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 李尧
 * @since  0.3.0
 */
public class CommonUtil {

    private static final SerializerFeature[] feature = {
            SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteDateUseDateFormat,
            SerializerFeature.DisableCircularReferenceDetect };

    // 参数校验
    public static String errorMessage(Errors errors) {
        StringBuilder sb = new StringBuilder();
        for (FieldError error : errors.getFieldErrors()) {
            sb.append(error.getField()).append("不符合验证规则").append(", ");
        }
        return sb.toString().substring(0, sb.length() - 2);
    }

}
