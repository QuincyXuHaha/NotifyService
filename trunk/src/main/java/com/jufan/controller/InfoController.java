package com.jufan.controller;

import com.alibaba.fastjson.JSON;
import com.jufan.main.factory.switches.Switches;
import com.jufan.main.runner.RunnablePuller;
import com.jufan.model.CommonResp;
import com.jufan.monitor.ResendDepotMonitor;
import com.jufan.monitor.UpdateDepotMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 信息相关
 *
 * 供外部获取系统内部各种信息，如各种仓库的库存等信息、各worker的工作状态等
 *
 * @author 李尧
 * @since  0.3.1
 */
@RestController
@RequestMapping(value = "/info")
public class InfoController {

    @Autowired
    private ResendDepotMonitor resendDepotMonitor;

    @Autowired
    private UpdateDepotMonitor updateDepotMonitor;

    @Autowired
    private RunnablePuller runnablePuller;

    // ResendDepot info
    @GetMapping("/resendDepotInfo")
    public CommonResp resendDepotInfo() {
        return new CommonResp(0, JSON.toJSONString(resendDepotMonitor.refreshAndGet()));
    }

    // UpdateDepot info
    @GetMapping("/updateDepotInfo")
    public CommonResp updateDepotInfo() {
        return new CommonResp(0, JSON.toJSONString(updateDepotMonitor.refreshAndGet()));
    }

    // workers状态一览
    @GetMapping("/workersStatus")
    public CommonResp view() {
        Map<String, String> cont = new HashMap<>();
        cont.put("Puller", Switches.PULLER_SWITCH ? "on" : "off");
        cont.put("ResendConsumer", Switches.RESEND_CONSUMER_SWITCH ? "on" : "off");
        cont.put("DepotSync", Switches.DEPOT_SYNC_SWITCH ? "on" : "off");
        cont.put("UpdateConsumer", Switches.UPDATE_CONSUMER_SWITCH ? "on" : "off");

        return new CommonResp(0, JSON.toJSONString(cont));
    }

    // Puller库存
    @GetMapping("/pullerStock")
    public int pullerStock() {
        return runnablePuller.getStock();
    }

}
