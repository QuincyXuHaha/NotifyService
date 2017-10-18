package com.jufan.monitor;

import com.jufan.main.factory.depot.ResendDepot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *
 * {@link ResendDepot}的监视器
 *
 * @author 李尧
 * @since  0.1.0
 */
@Component
public class ResendDepotMonitor {

    @Autowired
    private ResendDepot resendDepot;

    // 当前库存
    private Integer depotStock;

    // 当前FirstSend数量

    private Integer firstSendCount;

    // 当前Send数量
    private Integer sendCount;

    // 当前Finished数量
    private Integer finishedCount;

    // 当前Stopped数量
    private Integer stoppedCount;

    // 当前HttpSend数量
    private Integer httpSendCount;

    public ResendDepotMonitor refreshAndGet() {
        resendDepot.refreshInfo(this);
        return this;
    }

    public Integer getDepotStock() {
        return depotStock;
    }

    public void setDepotStock(Integer depotStock) {
        this.depotStock = depotStock;
    }

    public Integer getFirstSendCount() {
        return firstSendCount;
    }

    public void setFirstSendCount(Integer firstSendCount) {
        this.firstSendCount = firstSendCount;
    }

    public Integer getSendCount() {
        return sendCount;
    }

    public void setSendCount(Integer sendCount) {
        this.sendCount = sendCount;
    }

    public Integer getFinishedCount() {
        return finishedCount;
    }

    public void setFinishedCount(Integer finishedCount) {
        this.finishedCount = finishedCount;
    }

    public Integer getStoppedCount() {
        return stoppedCount;
    }

    public void setStoppedCount(Integer stoppedCount) {
        this.stoppedCount = stoppedCount;
    }

    public Integer getHttpSendCount() {
        return httpSendCount;
    }

    public void setHttpSendCount(Integer httpSendCount) {
        this.httpSendCount = httpSendCount;
    }
}
