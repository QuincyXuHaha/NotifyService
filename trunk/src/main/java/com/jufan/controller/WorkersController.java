package com.jufan.controller;

import com.jufan.main.factory.switches.Switches;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Worker控制相关
 *
 * 目前的功能就是开关主要工作线程
 *
 * @author 李尧
 * @since  0.3.1
 */
@RestController
@RequestMapping(value = "/worker")
public class WorkersController {

    /**
     *
     * Puller
     *
     **/
    @GetMapping("/puller/on")
    public String pullerOn() {
        Switches.PULLER_SWITCH = true;
        return "success";
    }

    @GetMapping("/puller/off")
    public String pullerOff() {
        Switches.PULLER_SWITCH = false;
        return "success";
    }

    /**
     *
     * ResendConsumer
     *
     **/
    @GetMapping("/resendConsumer/on")
    public String resendConsumerOn() {
        Switches.RESEND_CONSUMER_SWITCH = true;
        return "success";
    }

    @GetMapping("/resendConsumer/off")
    public String resendConsumerOff() {
        Switches.RESEND_CONSUMER_SWITCH = false;
        return "success";
    }

    /**
     *
     * DepotSync
     *
     **/
    @GetMapping("/depotSync/on")
    public String depotSyncOn() {
        Switches.DEPOT_SYNC_SWITCH = true;
        return "success";
    }

    @GetMapping("/depotSync/off")
    public String depotSyncOff() {
        Switches.DEPOT_SYNC_SWITCH = false;
        return "success";
    }

    /**
     *
     * UpdateConsumer
     *
     **/
    @GetMapping("/updateConsumer/on")
    public String updateConsumerOn() {
        Switches.UPDATE_CONSUMER_SWITCH = true;
        return "success";
    }

    @GetMapping("/updateConsumer/off")
    public String updateConsumerOff() {
        Switches.UPDATE_CONSUMER_SWITCH = false;
        return "success";
    }

}
