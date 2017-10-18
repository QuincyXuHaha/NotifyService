package com.jufan.entity;

import com.jufan.model.MessageModel;
import com.jufan.util.BeanUtil;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * 一个实体类
 *
 * @author 李尧
 * @since  0.1.0
 */
@Entity
@Table(name = "notify_info")
public class MessageEntity implements Serializable {

    private static final long serialVersionUID = -7898194272883238671L;

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;

    // 业务方ID，与topic构成联合唯一约束
    @Column(name = "ref_id", nullable = false)
    private String refId;

    // 自定义消息主题，与ref_id构成联合唯一约束
    @Column(nullable = false)
    private String topic;

    // 自定义消息副主题
    @Column(name = "sub_topic", nullable = false)
    private String subTopic;

    // 消息来源，如"xx系统"
    @Column(nullable = false)
    private String source;

    // 消息发送目标URI，标准格式:"http://www.xx.com:80/path1/path2"
    @Column(nullable = false)
    private String target;

    // -1 待发送, 0 正确结束(发送消息后收到目标响应而中断重发), 1 发送中, 2 自然结束(重发期间未收到目标响应，按照重发策略自然结束)
    @Column(nullable = false)
    private Integer status;

    // 请求类型(POST, GET..)
    @Column(name = "send_method", nullable = false)
    private String sendMethod;

    // Content-Type, 1 application/x_www_form_urlencoded, 2 application/json
    @Column(name = "content_type", nullable = false)
    private Integer contentType;

    // 重发策略：用英文逗号分隔的发送间隔秒数,如： "0,1,3,5,7,9". 必须从0开始
    @Column(name = "resend_strategy", nullable = false)
    private String resendStrategy;

    // 当前消息重发等级, 对应重发策略中被逗号分隔的单个元素
    @Column(name = "now_level", nullable = false)
    private String nowLevel;

    // 首次发送时间
    @Column(name = "firstsend_time", nullable = false)
    private Date firstSendTime;

    // 最近一次发送时间
    @Column(name = "lastsend_time")
    private Date lastSendTime;

    // 内容，序列化后的键值传参，如"a=1&b=2&c=3"
    private String content;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    public MessageModel toModel() {
        MessageModel messageModel = new MessageModel();
        BeanUtil.attrTransfer(this, messageModel);
        return messageModel;
    }

    public String getId() {
        return id;
    }

    public MessageEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getRefId() {
        return refId;
    }

    public MessageEntity setRefId(String refId) {
        this.refId = refId;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public MessageEntity setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public MessageEntity setSubTopic(String subTopic) {
        this.subTopic = subTopic;
        return this;
    }

    public String getSource() {
        return source;
    }

    public MessageEntity setSource(String source) {
        this.source = source;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public MessageEntity setTarget(String target) {
        this.target = target;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public MessageEntity setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getSendMethod() {
        return sendMethod;
    }

    public MessageEntity setSendMethod(String sendMethod) {
        this.sendMethod = sendMethod;
        return this;
    }

    public Integer getContentType() {
        return contentType;
    }

    public MessageEntity setContentType(Integer contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getResendStrategy() {
        return resendStrategy;
    }

    public MessageEntity setResendStrategy(String resendStrategy) {
        this.resendStrategy = resendStrategy;
        return this;
    }

    public String getNowLevel() {
        return nowLevel;
    }

    public MessageEntity setNowLevel(String nowLevel) {
        this.nowLevel = nowLevel;
        return this;
    }

    public Date getFirstSendTime() {
        return firstSendTime;
    }

    public MessageEntity setFirstSendTime(Date firstSendTime) {
        this.firstSendTime = firstSendTime;
        return this;
    }

    public Date getLastSendTime() {
        return lastSendTime;
    }

    public MessageEntity setLastSendTime(Date lastSendTime) {
        this.lastSendTime = lastSendTime;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MessageEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public MessageEntity setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public MessageEntity setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
