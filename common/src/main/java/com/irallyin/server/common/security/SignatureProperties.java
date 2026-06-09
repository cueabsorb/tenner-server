package com.irallyin.server.common.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "irallyin.signature")
public class SignatureProperties {
    private boolean enabled = true;
    private long timestampToleranceMs = 300000;
    private long nonceTtlSeconds = 360;
    private Map<String, AppCredential> apps = new HashMap<>();
    private List<String> excludedPaths = List.of(
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/admin",
            "/api/admin"
    );

    @Data
    public static class AppCredential {
        private String publicKey;
    }
}
