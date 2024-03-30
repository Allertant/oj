package com.shiyixi.ojbackendemailservice.strategy;

import com.shiyixi.ojbackendemailservice.model.EmailSendObj;

public class LogEmailStrategy implements EmailStrategy{
    @Override
    public EmailSendObj sendEmail(String message, int arg) {
        Integer code = Integer.parseInt(message);

        String html = String.format("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Document</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <p>感谢您注册 小兮OJ 账号，您的验证码为</p>\n" +
                "    <br/> \n" +
                "    <h1 style='font-size: 60px;font-weight:bold;text-align:center'>%s</h1>\n" +
                "</body>\n" +
                "</html>", code);

        String type = "text/html;charset=utf-8";

        EmailSendObj eMailSendObj = new EmailSendObj();
        eMailSendObj.setMessage(html);
        eMailSendObj.setType(type);
        eMailSendObj.setSubject("验证码");

        return eMailSendObj;
    }
}
