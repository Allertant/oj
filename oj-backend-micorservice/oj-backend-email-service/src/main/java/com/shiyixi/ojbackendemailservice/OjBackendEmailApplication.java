package com.shiyixi.ojbackendemailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) // 移除数据源配置
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.shiyixi.ojbackendserviceclient.service"}) // 注册 feignClient bean
public class OjBackendEmailApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjBackendEmailApplication.class, args);
    }

}
