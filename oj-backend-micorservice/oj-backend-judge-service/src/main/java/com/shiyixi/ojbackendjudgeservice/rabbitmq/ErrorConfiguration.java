package com.shiyixi.ojbackendjudgeservice.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 当配置了 retry 后才生效
@ConditionalOnProperty(prefix= "spring.rabbitmq.listener.simple.retry", name = "enabled", havingValue = "true")
@Configuration
public class ErrorConfiguration {
    @Bean
    public DirectExchange errorExchange() {
        return new DirectExchange("error.direct");
    }
    @Bean
    public Queue errorQueue() {
        return new Queue("error.queue");
    }
    @Bean
    public Binding errorBinding(Queue errorQueue, DirectExchange errorExchange) {
        return BindingBuilder.bind(errorQueue).to(errorExchange).with("error");
    }
    /**
     * 当消息重试失败后，则直接将这个消息发送到这个交换机中
     * @param rabbitTemplate rabbitTemplate
     * @return 自定义的消息器
     */
    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
    }
}
