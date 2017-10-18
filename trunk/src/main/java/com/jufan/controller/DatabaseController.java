package com.jufan.controller;

import com.jufan.model.CommonResp;
import com.jufan.model.MessageModel;
import com.jufan.service.message.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 消息数据库相关API
 *
 * TODO 用Netty做Server
 *
 * @author 李尧
 * @since  0.3.1
 */
@RestController
@RequestMapping(value = "/message")
public class DatabaseController {

    @Autowired
    private MessageService messageService;

    // 入库
    @PostMapping("/saveNew")
    public CommonResp saveNew(@Valid @RequestBody MessageModel messageModel, Errors errors) {
        return messageService.saveNew(messageModel);
    }

    // 查询
    @GetMapping("/query")
    public CommonResp query(String id, String source, Integer status) {
        return messageService.query(id, source, status);
    }

    // ...

}
