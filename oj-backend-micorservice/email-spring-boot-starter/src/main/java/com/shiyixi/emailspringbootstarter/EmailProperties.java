package com.shiyixi.emailspringbootstarter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "email.config")
public class EmailProperties {
    private String username;
    private String password;
    private String host = "";
    private Integer port = 25;
    private Boolean auth;
    private Boolean degub;
}
