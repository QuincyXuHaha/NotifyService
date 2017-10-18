package com.jufan.main.factory.switches;

        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;

/**
 *
 * 工作线程开关
 *
 * @author 李尧
 * @since  0.3.1
 */
public class Switches {

    private static final Logger LOGGER = LoggerFactory.getLogger(Switches.class);

    public static volatile boolean PULLER_SWITCH = true;

    public static volatile boolean RESEND_CONSUMER_SWITCH = true;

    public static volatile boolean DEPOT_SYNC_SWITCH = true;

    public static volatile boolean UPDATE_CONSUMER_SWITCH = true;

    public static void turnOn() {
        PULLER_SWITCH = true;
        RESEND_CONSUMER_SWITCH = true;
        DEPOT_SYNC_SWITCH = true;
        UPDATE_CONSUMER_SWITCH = true;
    }

    public static void shutdown(Exception e) {
        PULLER_SWITCH = false;
        RESEND_CONSUMER_SWITCH = false;
        DEPOT_SYNC_SWITCH = false;
        UPDATE_CONSUMER_SWITCH = false;
        LOGGER.error("【【CRASHED】】", e);
    }

}
