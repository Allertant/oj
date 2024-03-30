package com.shiyixi.ojbackendjudgeservice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.shiyixi.ojbackendjudgeservice.judge.JudgeService;
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
    private JudgeService judgeService;

    @SneakyThrows
    @RabbitListener(queues = {"code_queue"}) // 指定消费队列；设置自动确认
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag) {
        log.info("receiveMessage, message: {}", message);
        long questionSubmitId = Long.parseLong(message);

        judgeService.doJudge(questionSubmitId);

        /* try {
            judgeService.doJudge(questionSubmitId);
            channel.basicAck(deliverTag, false);
        } catch (BusinessException e) {
            if (e.getCode() == ErrorCode.PARAMS_ERROR.getCode()) {
                // 如果是参数错误，则不需要重复处理，直接放行
                log.info("消息处理失败，因为 questionSubmitId 不存在");
                channel.basicAck(deliverTag, false);
            }
        } catch (Exception e) {
            // 写数据库异常、有其他线程正在处理的异常、服务器中途宕机等
            channel.basicNack(deliverTag, false, false);
        }*/

    }
}
