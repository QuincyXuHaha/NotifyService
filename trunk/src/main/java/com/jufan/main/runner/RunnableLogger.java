package com.jufan.main.runner;

import com.jufan.dao.repository.NetLogRepo;
import com.jufan.entity.NetLogEntity;

/**
 *
 * 用于记录Http报文的Task
 *
 * @author 李尧
 * @since  0.2.0
 */
public class RunnableLogger implements Runnable {

    private NetLogRepo netLogRepo;

    private NetLogEntity netLog;

    public RunnableLogger(NetLogRepo netLogRepo, NetLogEntity netLog) {
        this.netLogRepo = netLogRepo;
        this.netLog = netLog;
    }

    @Override
    public void run() {
        netLogRepo.save(netLog);
    }
}
