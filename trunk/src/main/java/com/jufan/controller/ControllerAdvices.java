package com.jufan.controller;

import com.jufan.model.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * 异常处理器
 *
 * @author 李尧
 * @since  0.3.2
 */
@ControllerAdvice
public class ControllerAdvices {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public
    @ResponseBody CommonResp exceptionHandler(Exception e) {

        logger.error("ExceptionHandler: ", e);

        return new CommonResp(1, "System error");
    }

}
