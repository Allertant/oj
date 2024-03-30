package com.shiyixi.ojbackenduserservice.rabbitmq;

import com.shiyixi.ojbackendmodel.email.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;

@Component
@Slf4j
public class MyMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 向 rabbitMq 发送消息
     *
     * @param exchange   交换机
     * @param routingKey 路由 key
     * @param emailRequest    具体消息
     */
    public void sendMessage(String exchange, String routingKey, EmailRequest emailRequest) {
        /*
         * correlationData 中存储了消息唯一标识、成功回调、失败回调
         * 唯一标识可以不指定，默认使用 UUID
         */
        CorrelationData cd = new CorrelationData();
        // future 是多线程中的知识，拿到这个对象并不代表拿到了结果，要等到线程执行完成才能拿到其中的结果
        cd.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            /**
             * 是 spring 内部失败，不是 mq 返回的消息发送失败结果
             * @param ex 异常信息
             */
            @Override
            public void onFailure(@NotNull Throwable ex) {
                log.info("系统错误");
            }

            /**
             * mq 返回的结果
             * @param result Confirm 信息
             */
            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                if (result.isAck()) {
                    /*
                     * ack:
                     * 1. 达到交换机未被路由，但是会返回 ack + returns
                     * 2. 路由成功
                     * - 临时消息 入队
                     * - 持久消息 入队+持久化
                     */
                    log.info("消息发送成功，消息唯一标识：{}", result);
                } else {
                    /*
                     * 除了上述 ack 的情况都为 nack
                     */
                    log.info("消息发送失败，失败原因：{}", result.getReason());
                    // todo 有限次的重试
                }
            }
        });
        rabbitTemplate.convertAndSend(exchange, routingKey, emailRequest, cd);
    }
}
