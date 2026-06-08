package com.irallyin.server.core.auth.service;

import com.irallyin.server.common.exception.BusinessException;
import com.irallyin.server.core.auth.dto.GoogleTokenInfoResponse;
import com.irallyin.server.core.auth.dto.GoogleTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {
    private static final Set<String> TRUSTED_ISSUERS = Set.of("https://accounts.google.com", "accounts.google.com");

    private final RestClient.Builder restClientBuilder;

    @Value("${google.oauth.client-id}")
    private String clientId;

    @Value("${google.oauth.client-secret:}")
    private String clientSecret;

    public GoogleTokenResponse exchangeAuthorizationCode(
            String authorizationCode,
            String redirectUri,
            String codeVerifier
    ) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", authorizationCode);
        form.add("client_id", clientId);
        if (StringUtils.hasText(clientSecret)) {
            form.add("client_secret", clientSecret);
        }
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");
        if (StringUtils.hasText(codeVerifier)) {
            form.add("code_verifier", codeVerifier);
        }

        GoogleTokenResponse token = restClientBuilder.build()
                .post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(GoogleTokenResponse.class);

        if (token == null || !StringUtils.hasText(token.getIdToken())) {
            log.error("Google OAuth token exchange failed: token response is null or missing id_token");
            throw new BusinessException(10030, "Google授权失败，请重试");
        }
        return token;
    }

    public GoogleTokenInfoResponse verifyIdToken(String idToken) {
        GoogleTokenInfoResponse tokenInfo = restClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("oauth2.googleapis.com")
                        .path("/tokeninfo")
                        .queryParam("id_token", idToken)
                        .build())
                .retrieve()
                .body(GoogleTokenInfoResponse.class);

        if (tokenInfo == null
                || !StringUtils.hasText(tokenInfo.getSub())
                || !TRUSTED_ISSUERS.contains(tokenInfo.getIss())
                || !clientId.equals(tokenInfo.getAud())
                || tokenInfo.getExp() == null
                || tokenInfo.getExp() <= Instant.now().getEpochSecond()) {
            log.error("Google ID token verification failed: iss={}, aud={}, exp={}",
                    tokenInfo != null ? tokenInfo.getIss() : "null",
                    tokenInfo != null ? tokenInfo.getAud() : "null",
                    tokenInfo != null ? tokenInfo.getExp() : "null");
            throw new BusinessException(10031, "Google身份校验失败");
        }
        return tokenInfo;
    }
}
