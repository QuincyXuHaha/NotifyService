package com.jufan.service.message;

import com.jufan.model.CommonResp;
import com.jufan.model.MessageModel;

/**
 *
 * Message数据库相关Service
 *
 * @author 李尧
 * @since  0.3.2
 */
public interface MessageService {

    CommonResp saveNew(MessageModel message);

    CommonResp query(String id, String source, Integer status);

}
