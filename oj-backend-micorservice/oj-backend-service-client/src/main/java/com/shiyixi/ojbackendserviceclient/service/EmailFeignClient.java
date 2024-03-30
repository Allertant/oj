package com.shiyixi.ojbackendserviceclient.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "oj-backend-email-service", path = "/api/email/inner")
public interface EmailFeignClient {

    @PostMapping("/do")
    boolean sendEmail(@RequestParam("to") String to,
                      @RequestParam("typeEnum") String typeEnum,
                      @RequestParam("message") String message,
                      @RequestParam("arg") int arg);
}
