package com.shiyixi.ojbackenduserservice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.shiyixi.ojbackendmodel.email.EmailRequest;
import com.shiyixi.ojbackendserviceclient.service.EmailFeignClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MyMessageConsumer {
    @Resource
    private EmailFeignClient emailFeignClient;

    @SneakyThrows
    @RabbitListener(queues = {"verifyCode_queue"}) // 指定消费队列；设置自动确认
    public void receiveMessage(EmailRequest emailRequest, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag) {
        log.info("receiveMessage, message: {}", emailRequest);
        String message = emailRequest.getMessage();
        String emailType = emailRequest.getEmailType();
        String to = emailRequest.getTo();
        int arg = emailRequest.getArg();

        emailFeignClient.sendEmail(to, emailType, message, arg);
    }
}
