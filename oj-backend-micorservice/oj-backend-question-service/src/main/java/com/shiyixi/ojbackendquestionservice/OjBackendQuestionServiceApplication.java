package com.shiyixi.ojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.shiyixi.ojbackendquestionservice.mapper") // mapper 扫描
@EnableScheduling // 开启定时任务配置
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true) // 代理配置
@ComponentScan("com.shiyixi") //其他模块的 bean 也要扫描到
@EnableDiscoveryClient // nacos 配置
@EnableFeignClients(basePackages = {"com.shiyixi.ojbackendserviceclient.service"}) // 注册 feignClient bean
public class OjBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjBackendQuestionServiceApplication.class, args);
    }

}
