package com.shiyixi.ojbackendquestionservice.conofig;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 这个类的重要作用是为了重新添加拦截器，因为之前的 SentinelWebIntercepter 被 WebMvcConfigurationSupport 覆盖了
 */
// @Component
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SentinelWebInterceptor()).addPathPatterns("/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
