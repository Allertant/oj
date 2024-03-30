package com.shiyixi.emailspringbootstarter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Data
@Slf4j
public class EmailTemplate {
    private EmailProperties properties;

    private Session getSession() {
        String username = properties.getUsername();
        String password = properties.getPassword();
        String host = properties.getHost();
        Boolean auth = properties.getAuth();
        Boolean degub = properties.getDegub();

        // 获取系统属性
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", host);
        props.put("mail.smtp.auth", String.valueOf(auth));
        props.put("mail.user", username);

        // 获取默认的 Session 对象。
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        session.setDebug(degub);
        return session;
    }

    public boolean sendEmail(String to, String subject, String msg, String type) {
        String username = properties.getUsername();

        Session session = getSession();

        try {
            // 创建默认的 MimeMessage 对象。
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(username));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: 头字段
            message.setSubject(subject, "UTF-8");

            // 发送消息
            message.setContent(msg, type);
            // 发送消息
            Transport.send(message);
            log.info("发送验证码成功，邮箱：{}", to);
        } catch (MessagingException mex) {
            log.info("发送验证码失败，邮箱：{}", to);
            return false;
        }
        return true;
    }
}
