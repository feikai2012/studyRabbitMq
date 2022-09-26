package com.study.rabbitmq.callbackretry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Jonesw
 * @description
 * @date 2022-09-23
 */
@SpringBootApplication(scanBasePackages = {"com.study.rabbitmq"})
public class CallbackApplication {
    public static void main(String[] args) {
        SpringApplication.run(CallbackApplication.class, args);
    }
}
