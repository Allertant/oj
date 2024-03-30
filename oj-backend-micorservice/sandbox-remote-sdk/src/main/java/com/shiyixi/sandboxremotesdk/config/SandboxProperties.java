package com.shiyixi.sandboxremotesdk.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SandboxProperties.PREFIX)
public class SandboxProperties {
    public static final String PREFIX = "sandbox";

    private String url = "http://192.168.159.133:8085";
    private String auth;
    private String secretKey;
}
