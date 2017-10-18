package com.jufan.util;

/**
 * @author 李尧
 * @since  0.2.0
 */
public enum ResendCheckState {

    FIRSTSEND,      // 首次发送
    FINISHED,       // 已重发完毕
    WAIT,           // 时间未到，继续等待
    SEND            // 可以发送

}
