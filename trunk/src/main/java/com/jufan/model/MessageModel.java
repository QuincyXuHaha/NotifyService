package com.jufan.model;

import com.jufan.entity.MessageEntity;
import com.jufan.util.BeanUtil;

import javax.persistence.Column;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * MessageEntity对应的Model操作类
 *
 * <线程安全>
 *
 * @author 李尧
 * @since  0.2.0
 */
public class MessageModel implements Serializable, Cloneable {

    private static final long serialVersionUID = -7898194272883238672L;

    // 读写锁
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Null
    private String id;

    // 业务方ID，与topic构成联合唯一约束
    @NotNull
    @Size(min = 1, max = 200)
    private String refId;

    // 自定义消息主题，与ref_id构成联合唯一约束
    @NotNull
    @Size(min = 1, max = 200)
    private String topic;

    // 自定义消息副主题
    @NotNull
    @Size(min = 1, max = 200)
    private String subTopic;

    // 消息来源，如"xx系统"
    @NotNull
    @Size(min = 1, max = 100)
    private String source;

    // 消息发送目标URI，标准格式:"http://www.xx.com:80/path1/path2"
    @NotNull
    @Size(min = 1, max = 300)
    private String target;

    // -1 待发送, 0 正确结束(发送消息后收到目标响应而中断重发), 1 发送中, 2 自然结束(重发期间未收到目标响应，按照重发策略自然结束)
    @NotNull
    @Digits(integer = 10, fraction = 0)
    private Integer status = -1;

    // 请求类型(POST, GET..)
    @NotNull
    @Size(min = 1, max = 10)
    private String sendMethod;

    // Content-Type, 1 application/x-www-form-urlencoded, 2 application/json
    @NotNull
    @Digits(integer = 10, fraction = 0)
    private Integer contentType;

    // 重发策略：用英文逗号分隔的发送间隔秒数,如： "0,1,3,5,7,9". 必须从0开始
    @NotNull
    @Size(min = 1, max = 300)
    @Pattern(regexp = "^0{1}(,\\d+)*$")
    private String resendStrategy;

    // 当前消息重发等级, 对应重发策略中被逗号分隔的单个元素
    @NotNull
    @Size(min = 1, max = 10)
    private String nowLevel;

    // 首次发送时间
    @NotNull
    private Date firstSendTime;

    // 最近一次发送时间
    @Null
    private Date lastSendTime;

    // 内容，序列化后的键值传参，如"a=1&b=2&c=3"
    private String content = "";

    @Null
    private Date createTime;

    @Null
    private Date updateTime;

    /**
     * 在发送http请求以及入库nofity_net_log时需要的是发送的即时发送等级
     *
     * 为了防止入库时nowLevel已经发生改变，所以设此字段保存发送前的nowLevel
     */
    private String sendLevel;

    public boolean valid() {
        if (!this.getNowLevel().matches("^\\d+$") && !"finished".equals(this.getNowLevel()))
            return false;
        if (!this.getResendStrategy().matches("^0{1}(,\\d+)*$"))
            return false;

        return true;
    }

    public MessageEntity toEntity() {
        try {
            lock.readLock().lock();
            MessageEntity messageEntity = new MessageEntity();
            // TODO 暂时不会在并发情况调用，暂不修改
            BeanUtil.attrTransfer(this, messageEntity);
            return messageEntity;
        } finally {
            lock.readLock().unlock();
        }
    }

    public MessageModel clone() {
        try {
            lock.readLock().lock();
            return (MessageModel) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return new MessageModel();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean equals(MessageModel message) {

        return this == message ||
                (this.getId().equals(message.getId()) &&
                this.getRefId().equals(message.getRefId()) &&
                this.getTopic().equals(message.getTopic()) &&
                this.getSubTopic().equals(message.getSubTopic()) &&
                this.getSource().equals(message.getSource()) &&
                this.getTarget().equals(message.getTarget()) &&
                this.getStatus().equals(message.getStatus()) &&
                this.getSendMethod().equals(message.getSendMethod()) &&
                this.getContentType().equals(message.getContentType()) &&
                this.getResendStrategy().equals(message.getResendStrategy()) &&
                this.getNowLevel().equals(message.getNowLevel()) &&
                this.getFirstSendTime().equals(message.getFirstSendTime()) &&
                this.getLastSendTime().equals(message.getLastSendTime()) &&
                this.getContent().equals(message.getContent()) &&
                this.getCreateTime().equals(message.getCreateTime()) &&
                this.getUpdateTime().equals(message.getUpdateTime()));
    }

    public String getRefId() {
        try {
            lock.readLock().lock();
            return refId;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setRefId(String refId) {
        lock.writeLock().lock();
        this.refId = refId;
        lock.writeLock().unlock();
    }

    public String getTopic() {
        try {
            lock.readLock().lock();
            return topic;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setTopic(String topic) {
        lock.writeLock().lock();
        this.topic = topic;
        lock.writeLock().unlock();
    }

    public String getSubTopic() {
        try {
            lock.readLock().lock();
            return subTopic;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setSubTopic(String subTopic) {
        lock.writeLock().lock();
        this.subTopic = subTopic;
        lock.writeLock().unlock();
    }

    public String getId() {
        try {
            lock.readLock().lock();
            return id;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setId(String id) {
        lock.writeLock().lock();
        this.id = id;
        lock.writeLock().unlock();
    }

    public Date getCreateTime() {
        try {
            lock.readLock().lock();
            return createTime;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setCreateTime(Date createTime) {
        lock.writeLock().lock();
        this.createTime = createTime;
        lock.writeLock().unlock();
    }

    public Date getUpdateTime() {
        try {
            lock.readLock().lock();
            return updateTime;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setUpdateTime(Date updateTime) {
        lock.writeLock().lock();
        this.updateTime = updateTime;
        lock.writeLock().unlock();
    }

    public String getSource() {
        try {
            lock.readLock().lock();
            return source;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setSource(String source) {
        lock.writeLock().lock();
        this.source = source;
        lock.writeLock().unlock();
    }

    public Integer getStatus() {
        try {
            lock.readLock().lock();
            return status;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setStatus(Integer status) {
        lock.writeLock().lock();
        this.status = status;
        lock.writeLock().unlock();
    }

    public String getTarget() {
        try {
            lock.readLock().lock();
            return target;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setTarget(String target) {
        lock.writeLock().lock();
        this.target = target;
        lock.writeLock().unlock();
    }

    public String getSendMethod() {
        try {
            lock.readLock().lock();
            return sendMethod;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setSendMethod(String sendMethod) {
        lock.writeLock().lock();
        this.sendMethod = sendMethod;
        lock.writeLock().unlock();
    }

    public Integer getContentType() {
        try {
            lock.readLock().lock();
            return contentType;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setContentType(Integer contentType) {
        lock.writeLock().lock();
        this.contentType = contentType;
        lock.writeLock().unlock();
    }

    public String getResendStrategy() {
        try {
            lock.readLock().lock();
            return resendStrategy;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setResendStrategy(String resendStrategy) {
        lock.writeLock().lock();
        this.resendStrategy = resendStrategy;
        lock.writeLock().unlock();
    }

    public String getNowLevel() {
        try {
            lock.readLock().lock();
            return nowLevel;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setNowLevel(String nowLevel) {
        lock.writeLock().lock();
        this.nowLevel = nowLevel;
        lock.writeLock().unlock();
    }

    public Date getFirstSendTime() {
        try {
            lock.readLock().lock();
            return firstSendTime;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setFirstSendTime(Date firstSendTime) {
        lock.writeLock().lock();
        this.firstSendTime = firstSendTime;
        lock.writeLock().unlock();
    }

    public Date getLastSendTime() {
        try {
            lock.readLock().lock();
            return lastSendTime;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setLastSendTime(Date lastSendTime) {
        lock.writeLock().lock();
        this.lastSendTime = lastSendTime;
        lock.writeLock().unlock();
    }

    public String getContent() {
        try {
            lock.readLock().lock();
            return content;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setContent(String content) {
        lock.writeLock().lock();
        this.content = content;
        lock.writeLock().unlock();
    }

    public String getSendLevel() {
        try {
            lock.readLock().lock();
            return sendLevel;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setSendLevel(String sendLevel) {
        lock.writeLock().lock();
        this.sendLevel = sendLevel;
        lock.writeLock().unlock();
    }

}
