package com.irallyin.server.core.profile.service;

import com.irallyin.server.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class ProfileContentSafetyService {
    private static final List<String> LOCAL_SENSITIVE_WORDS = List.of("敏感词", "辱骂", "违法");

    @Value("${profile.content-safety.review-url:}")
    private String reviewUrl;

    public void assertSafeText(String value, String fieldLabel) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        if (!passesExternalSafetyReview(value)) {
            throw new BusinessException(12001, fieldLabel + "包含不可使用内容");
        }
        String normalized = value.toLowerCase();
        for (String word : LOCAL_SENSITIVE_WORDS) {
            if (normalized.contains(word.toLowerCase())) {
                throw new BusinessException(12001, fieldLabel + "包含不可使用内容");
            }
        }
    }

    private boolean passesExternalSafetyReview(String value) {
        if (!StringUtils.hasText(reviewUrl)) {
            return true;
        }
        try {
            String requestBody = "{\"content\":\"" + escapeJson(value) + "\"}";
            HttpRequest request = HttpRequest.newBuilder(URI.create(reviewUrl))
                    .timeout(Duration.ofSeconds(3))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return false;
            }
            String body = response.body() == null ? "" : response.body().replace(" ", "").toLowerCase();
            return !body.contains("\"pass\":false") && !body.contains("\"safe\":false");
        } catch (Exception e) {
            log.error("External content safety review failed: {}", e.getMessage(), e);
            return false;
        }
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
