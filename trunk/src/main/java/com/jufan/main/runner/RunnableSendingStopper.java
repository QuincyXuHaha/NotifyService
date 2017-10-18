package com.jufan.main.runner;

import com.jufan.main.factory.depot.ResendDepot;

/**
 *
 * 收到httpResponse后执行停止重发操作
 *
 * @author 李尧
 * @since  0.1.0
 */
public class RunnableSendingStopper implements Runnable {

    private ResendDepot resendDepot;

    private String msgId;

    public RunnableSendingStopper(ResendDepot resendDepot, String msgId) {
        this.resendDepot = resendDepot;
        this.msgId = msgId;
    }

    @Override
    public void run() {
        resendDepot.stopResending(msgId);
    }

}
