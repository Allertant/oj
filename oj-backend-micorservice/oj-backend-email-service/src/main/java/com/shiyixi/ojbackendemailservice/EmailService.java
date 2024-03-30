package com.shiyixi.ojbackendemailservice;

import com.shiyixi.emailspringbootstarter.EmailTemplate;
import com.shiyixi.ojbackendemailservice.model.EmailSendObj;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EmailService {
    @Resource
    private EmailTemplate emailTemplate;

    @Resource
    private EmailManager emailManager;

    /**
     * 发送验证码
     *
     * @param to 目的邮箱
     * @return 布尔值
     */
    public boolean sendMail(String to, String typeEnum, String message, int arg) {

        EmailSendObj emailSendObj = emailManager.getEmail(typeEnum,message, arg);
        message = emailSendObj.getMessage();
        String subject = emailSendObj.getSubject();
        String type = emailSendObj.getType();

        return emailTemplate.sendEmail(to, subject, message, type);
    }
}
