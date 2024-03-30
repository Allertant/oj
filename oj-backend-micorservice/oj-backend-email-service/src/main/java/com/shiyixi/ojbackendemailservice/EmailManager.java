package com.shiyixi.ojbackendemailservice;

import com.shiyixi.ojbackendemailservice.exception.EmailTypeNotFoundException;
import com.shiyixi.ojbackendemailservice.model.EmailSendObj;
import com.shiyixi.ojbackendemailservice.model.EmailTypeEnum;
import com.shiyixi.ojbackendemailservice.strategy.EmailStrategy;
import com.shiyixi.ojbackendemailservice.strategy.LogEmailStrategy;
import com.shiyixi.ojbackendemailservice.strategy.VerifyCodeEmailStrategy;
import org.springframework.stereotype.Service;

@Service
public class EmailManager {
    public EmailSendObj getEmail(String typeEnum, String message, int arg) {
        EmailStrategy emailStrategy;
        if (EmailTypeEnum.VERIFYCODE.getEmailType().equals(typeEnum)) {
            emailStrategy = new VerifyCodeEmailStrategy();
        }else if (EmailTypeEnum.LOG.getEmailType().equals(typeEnum)) {
            emailStrategy = new LogEmailStrategy();
        } else {
            throw new EmailTypeNotFoundException("该邮件类型不存在");
        }
        return emailStrategy.sendEmail(message, arg);
    }
}
