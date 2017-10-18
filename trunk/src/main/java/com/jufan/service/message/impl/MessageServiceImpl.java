package com.jufan.service.message.impl;

import com.alibaba.fastjson.JSON;
import com.jufan.dao.repository.MessageRepo;
import com.jufan.entity.MessageEntity;
import com.jufan.model.CommonResp;
import com.jufan.model.MessageModel;
import com.jufan.service.message.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Message数据库相关Service
 *
 * @author 李尧
 * @since  0.3.2
 */
@Service
public class MessageServiceImpl implements MessageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageRepo messageRepo;

    // 入库
    @Override
    public CommonResp saveNew(MessageModel message) {

        message.setCreateTime(new Date());
        String id = messageRepo.save(message.toEntity()).getId();

        return new CommonResp(0, id);
    }

    // 查询
    @Override
    public CommonResp query(String id, String source, Integer status) {

        if (id == null && source == null && status == null)
            return new CommonResp(1, "至少需要一个查询条件");

        // 动态查询条件
        Specification<MessageEntity> condition = (root, cq, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (id != null)
                predicates.add(cb.equal(root.get("id"), id));
            if (source != null)
                predicates.add(cb.equal(root.get("source"), source));
            if (status != null)
                predicates.add(cb.equal(root.get("status"), status));

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        List<MessageEntity> res = messageRepo.findAll(condition);

        return new CommonResp(0, JSON.toJSONString(res));
    }

}
