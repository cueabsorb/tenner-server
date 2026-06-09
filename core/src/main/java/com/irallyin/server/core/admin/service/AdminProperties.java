package com.irallyin.server.core.admin.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "irallyin.admin")
public class AdminProperties {
    private String email = "656619107@qq.com";
    private String password = "";
    private long tokenExpirationMs = 8 * 60 * 60 * 1000L;
}
