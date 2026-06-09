package com.irallyin.server.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestSignatureFilter extends OncePerRequestFilter {

    private final SignatureProperties signatureProperties;
    private final NonceCache nonceCache;
    private final ObjectMapper objectMapper;

    private static final String HEADER_APP_KEY = "X-Signature-AppKey";
    private static final String HEADER_TIMESTAMP = "X-Signature-Timestamp";
    private static final String HEADER_NONCE = "X-Signature-Nonce";
    private static final String HEADER_SIGNATURE = "X-Signature";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!signatureProperties.isEnabled()) {
            return true;
        }
        String path = requestPath(request);
        for (String excluded : signatureProperties.getExcludedPaths()) {
            if (path.startsWith(excluded)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String appKey = request.getHeader(HEADER_APP_KEY);
        String timestamp = request.getHeader(HEADER_TIMESTAMP);
        String nonce = request.getHeader(HEADER_NONCE);
        String signature = request.getHeader(HEADER_SIGNATURE);

        // --- 1. Validate required headers ---
        var missingHeaders = missingSignatureHeaders(appKey, timestamp, nonce, signature);
        if (!missingHeaders.isEmpty()) {
            writeError(request, response, 10040, "Missing required signature headers: " + String.join(", ", missingHeaders));
            return;
        }

        // --- 2. Validate timestamp tolerance ---
        long requestTimestamp;
        try {
            requestTimestamp = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            writeError(request, response, 10041, "Invalid timestamp format");
            return;
        }
        long now = System.currentTimeMillis();
        long tolerance = signatureProperties.getTimestampToleranceMs();
        if (Math.abs(now - requestTimestamp) > tolerance) {
            writeError(request, response, 10041, "Request timestamp expired");
            return;
        }

        // --- 3. Check nonce uniqueness ---
        if (nonceCache.exists(nonce)) {
            writeError(request, response, 10042, "Duplicate request nonce");
            return;
        }

        // --- 4. Look up public key by appKey ---
        SignatureProperties.AppCredential credential = signatureProperties.getApps().get(appKey);
        if (credential == null || credential.getPublicKey() == null || credential.getPublicKey().isBlank()) {
            writeError(request, response, 10043, "Unknown app key");
            return;
        }

        // --- 5. Wrap request to cache body ---
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        byte[] body = cachedRequest.getCachedBody();

        // --- 6. Build stringToSign ---
        String method = request.getMethod();
        String path = request.getRequestURI();
        String bodySha256Hex = sha256Hex(body);
        String stringToSign = method + "\n"
                + path + "\n"
                + timestamp + "\n"
                + nonce + "\n"
                + bodySha256Hex;

        // --- 7. Verify RSA signature ---
        try {
            PublicKey publicKey = parsePublicKey(credential.getPublicKey());
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(stringToSign.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = java.util.Base64.getDecoder().decode(signature);
            if (!sig.verify(signatureBytes)) {
                writeError(request, response, 10044, "Request signature verification failed");
                return;
            }
        } catch (Exception e) {
            log.warn("Signature verification error: {}", e.getMessage());
            writeError(request, response, 10044, "Request signature verification failed");
            return;
        }

        // --- 8. Store nonce ---
        nonceCache.store(nonce, signatureProperties.getNonceTtlSeconds());

        // --- 9. Continue ---
        filterChain.doFilter(cachedRequest, response);
    }

    private void writeError(HttpServletRequest request, HttpServletResponse response, int code, String message) throws IOException {
        log.warn("Request signature rejected: method={} path={} code={} message={}",
                request.getMethod(), request.getRequestURI(), code, message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("message", message);
        body.put("data", null);
        response.getWriter().write(objectMapper.writeValueAsString(body));
        response.getWriter().flush();
    }

    private static java.util.List<String> missingSignatureHeaders(
            String appKey,
            String timestamp,
            String nonce,
            String signature
    ) {
        java.util.List<String> missingHeaders = new ArrayList<>();
        if (appKey == null || appKey.isBlank()) {
            missingHeaders.add(HEADER_APP_KEY);
        }
        if (timestamp == null || timestamp.isBlank()) {
            missingHeaders.add(HEADER_TIMESTAMP);
        }
        if (nonce == null || nonce.isBlank()) {
            missingHeaders.add(HEADER_NONCE);
        }
        if (signature == null || signature.isBlank()) {
            missingHeaders.add(HEADER_SIGNATURE);
        }
        return missingHeaders;
    }

    private static String sha256Hex(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private String requestPath(HttpServletRequest request) {
        String path = request.getServletPath();
        if (path != null && !path.isBlank()) {
            return path;
        }

        path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && path.startsWith(contextPath)) {
            return path.substring(contextPath.length());
        }
        return path;
    }

    private static PublicKey parsePublicKey(String base64Der) throws Exception {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(base64Der);
        java.security.spec.X509EncodedKeySpec keySpec = new java.security.spec.X509EncodedKeySpec(keyBytes);
        java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}
