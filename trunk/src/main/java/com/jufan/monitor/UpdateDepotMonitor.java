package com.jufan.monitor;

import com.jufan.main.factory.depot.UpdateDepot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *
 * {@link UpdateDepot}的监视器
 *
 * @author 李尧
 * @since  0.1.0
 */
@Component
public class UpdateDepotMonitor {

    @Autowired
    private UpdateDepot updateDepot;

    private Integer depotStock;

    private Integer updateCount;

    private Integer poolLeft;

    public UpdateDepotMonitor refreshAndGet() {
        updateDepot.refreshInfo(this);
        return this;
    }

    public Integer getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(Integer updateCount) {
        this.updateCount = updateCount;
    }

    public Integer getPoolLeft() {
        return poolLeft;
    }

    public void setPoolLeft(Integer poolLeft) {
        this.poolLeft = poolLeft;
    }

    public Integer getDepotStock() {
        return depotStock;
    }

    public void setDepotStock(Integer depotStock) {
        this.depotStock = depotStock;
    }
}
