package com.shiyixi.ojbackendjudgeservice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class InitRabbitMq {

    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @PostConstruct
    public void doInit() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            Connection connection = factory.newConnection(); // 建立连接
            Channel channel = connection.createChannel(); // channel 操作消息队列的客户端，类似于 jdbc

            // 创建交换机
            String EXCHANGE_NAME = "code_exchange";
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            // 创建队列
            String queueName = "code_queue";
            channel.queueDeclare(queueName, true, false, false, null);

            // 绑定关系
            channel.queueBind(queueName, EXCHANGE_NAME, "my_routingKey");
            log.info("消息队列启动成功");
        } catch (Exception e) {
            log.error("消息队列启动失败" + host);
        }
    }
}

