package com.shiyixi.emailspringbootstarter.config;

import com.shiyixi.emailspringbootstarter.EmailProperties;
import com.shiyixi.emailspringbootstarter.EmailTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({EmailTemplate.class})
@EnableConfigurationProperties({EmailProperties.class})
public class EmailAutoConfiguration {
    @Resource
    private EmailProperties emailProperties;

    @Bean
    @ConditionalOnMissingBean
    public EmailTemplate emailTemplate() {
        EmailTemplate emailTemplate = new EmailTemplate();
        emailTemplate.setProperties(emailProperties);
        return emailTemplate;
    }

}
