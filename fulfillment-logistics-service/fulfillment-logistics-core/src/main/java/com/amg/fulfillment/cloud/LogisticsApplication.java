package com.amg.fulfillment.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * 服务启动类
 * */
@EnableDiscoveryClient
@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
public class LogisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsApplication.class, args);
        System.out.println("=================Application启动成功====================");
    }

}
