package com.shiyixi.ojbackendemailservice.controller.inner;

import com.shiyixi.ojbackendemailservice.EmailService;
import com.shiyixi.ojbackendserviceclient.service.EmailFeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/inner")
@RestController
public class EmailController implements EmailFeignClient {
    @Resource
    private EmailService emailService;

    @Override
    @PostMapping("/do")
    public boolean sendEmail(@RequestParam("to") String to, @RequestParam("typeEnum") String typeEnum,
                             @RequestParam("message") String message,
                             @RequestParam("arg") int arg) {
        return emailService.sendMail(to, typeEnum,message, arg);
    }
}
