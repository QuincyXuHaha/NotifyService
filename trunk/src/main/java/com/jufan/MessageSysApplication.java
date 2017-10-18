package com.jufan;

import com.jufan.config.CustomBeans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@Import(CustomBeans.class)
public class MessageSysApplication {

    public static void main(String[] args) {
		SpringApplication.run(MessageSysApplication.class, args);
    }
}
