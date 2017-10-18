package com.jufan.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * 一个实体类
 *
 * @author 李尧
 * @since  0.2.0
 */
@Entity
@Table(name = "notify_net_log")
public class NetLogEntity implements Serializable {

    private static final long serialVersionUID = -7898194272883238672L;

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    private String id;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    private Date updateTime;

    // 日志种类, 1 消息发送, 2 响应接收
    @Column(nullable = false)
    private Integer type;

    // 消息ID
    @Column(nullable = false)
    private String msgId;

    // 消息重发等级
    @Column(nullable = false)
    private String resendLevel;

    // http报文
    @Column(nullable = false)
    private String packet;

    public static NetLogEntity newRequestEntity(String msgId, String resendLevel, String packet) {
        NetLogEntity netLogEntity = new NetLogEntity();
        netLogEntity.setCreateTime(new Date());
        netLogEntity.setType(1);
        netLogEntity.setMsgId(msgId);
        netLogEntity.setResendLevel(resendLevel);
        netLogEntity.setPacket(packet);
        return netLogEntity;
    }

    public static NetLogEntity newResponseEntity(String msgId, String resendLevel, String packet) {
        NetLogEntity netLogEntity = new NetLogEntity();
        netLogEntity.setCreateTime(new Date());
        netLogEntity.setType(2);
        netLogEntity.setMsgId(msgId);
        netLogEntity.setResendLevel(resendLevel);
        netLogEntity.setPacket(packet);
        return netLogEntity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getResendLevel() {
        return resendLevel;
    }

    public void setResendLevel(String resendLevel) {
        this.resendLevel = resendLevel;
    }

    public String getPacket() {
        return packet;
    }

    public void setPacket(String packet) {
        this.packet = packet;
    }
}
