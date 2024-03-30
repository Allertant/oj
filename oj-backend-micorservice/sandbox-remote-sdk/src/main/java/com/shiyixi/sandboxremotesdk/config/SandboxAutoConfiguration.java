package com.shiyixi.sandboxremotesdk.config;

import com.shiyixi.sandboxremotesdk.RemoteSandboxClient;
import com.shiyixi.sandboxremotesdk.RemoteSandboxSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.rmi.Remote;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({SandboxProperties.class})
@EnableFeignClients(basePackages = {"com.shiyixi.sandboxremotesdk"})
public class SandboxAutoConfiguration {
    @Resource
    private SandboxProperties sandboxProperties;

    @Bean
    public RemoteSandboxSender remoteSandboxSender(RemoteSandboxClient remoteSandboxClient) {
        RemoteSandboxSender remoteSandboxSender = new RemoteSandboxSender();
        remoteSandboxSender.setRemoteSandboxClient(remoteSandboxClient);
        remoteSandboxSender.setSecretKey(sandboxProperties.getSecretKey());
        return remoteSandboxSender;
    }
}
