package com.jufan;

import com.jufan.main.initializer.ApplicationInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *
 * 应用初始化触发器
 *
 * @author 李尧
 * @since  0.1.0
 */
@Component
public class ApplicationStartedUpListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ApplicationInitializer applicationInitializer;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        applicationInitializer.init();
    }

}



