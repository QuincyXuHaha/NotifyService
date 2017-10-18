package com.jufan;

import com.jufan.dao.repository.MessageRepo;
import com.jufan.main.network.HttpClient;
import com.jufan.service.cache.MessageRedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageSysApplicationTests {

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private MessageRedisService messageRedisService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private HttpClient httpClient;

    @Test
    public void batch() throws Exception {

//        String a = HttpUtil.post("https://testing.dauron.com/webapp/pay/wecash/callback_url", "{\"actualAmount\":\"1.0000\",\"actualRepayTime\":\"2017-08-18 09:16:11\",\"billNumber\":\"CO-56PV-0003-RR-0017918\",\"channelName\":\"支付宝\",\"overdueDays\":\"0\",\"overdueStartDate\":\"2017-09-16\",\"repaymentStatus\":\"2\",\"signature\":\"338DA1CAD6C2B11C7F74E73F7E43FAFC\",\"status\":\"1\",\"timestamp\":\"1503018972154\"}");
//
//        SSLResponse sslResponse = HttpUtil.sslPost("https://testing.dauron.com/webapp/pay/wecash/callback_url", "{\"actualAmount\":\"1.0000\",\"actualRepayTime\":\"2017-08-18 09:16:11\",\"billNumber\":\"CO-56PV-0003-RR-0017918\",\"channelName\":\"支付宝\",\"overdueDays\":\"0\",\"overdueStartDate\":\"2017-09-16\",\"repaymentStatus\":\"2\",\"signature\":\"338DA1CAD6C2B11C7F74E73F7E43FAFC\",\"status\":\"1\",\"timestamp\":\"1503018972154\"}", 2);

    }
}

