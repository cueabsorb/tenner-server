package com.irallyin.server.core.auth.verification;

import com.irallyin.server.core.mail.template.EmailTemplate;
import com.irallyin.server.core.mail.template.EmailTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * High-level service to generate and validate verification codes.
 */
public class VerificationService {

    private static final Logger log = LoggerFactory.getLogger(VerificationService.class);

    private final VerificationCodeStore store;
    private final EmailSender emailSender; // may be null if not sending email
    private final EmailTemplateService templateService; // may be null
    private final SecureRandom random = new SecureRandom();

    // default TTL 5 minutes
    public static final long DEFAULT_TTL_SECONDS = TimeUnit.MINUTES.toSeconds(5);

    public VerificationService(VerificationCodeStore store, EmailSender emailSender) {
        this(store, emailSender, null);
    }

    public VerificationService(VerificationCodeStore store, EmailSender emailSender, EmailTemplateService templateService) {
        this.store = Objects.requireNonNull(store);
        this.emailSender = emailSender;
        this.templateService = templateService;
    }

    /**
     * Generate a 6-digit numeric verification code, store it under key and return it.
     */
    public String generateAndStore(String key) {
        return generateAndStore(key, DEFAULT_TTL_SECONDS);
    }

    public String generateAndStore(String key, long ttlSeconds) {
        Objects.requireNonNull(key);
        int code = 100000 + random.nextInt(900000);
        String s = Integer.toString(code);
        store.put(key, s, ttlSeconds);
        return s;
    }

    /**
     * Generate code and send to email address. The key should be the key under which the code will be stored.
     */
    public String generateAndSendEmail(String key, String toEmail, String subject, String bodyTemplate) throws Exception {
        String code = generateAndStore(key);
        if (emailSender == null) {
            log.error("Email sender not configured, cannot send verification email to {}", toEmail);
            throw new IllegalStateException("Email sender not configured");
        }
        try {
            String body = (bodyTemplate == null ? "Your verification code is: " + code : bodyTemplate.replace("{code}", code));
            emailSender.send(toEmail, subject == null ? "Your verification code" : subject, body);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage(), e);
            throw e;
        }
        return code;
    }

    /**
     * Generate a verification code, store it, render the verification template and send it.
     */
    public String generateAndSendVerificationEmail(String key, String toEmail) throws Exception {
        String code = generateAndStore(key);
        if (emailSender == null) {
            log.error("Email sender not configured, cannot send verification email to {}", toEmail);
            throw new IllegalStateException("Email sender not configured");
        }

        EmailTemplate tpl;
        if (templateService != null) {
            tpl = templateService.renderVerificationCode(code);
        } else {
            // fallback simple text
            tpl = new EmailTemplate("你的验证码", "你的验证码是 " + code);
        }

        try {
            emailSender.send(toEmail, tpl.getSubject(), tpl.getBody());
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage(), e);
            throw e;
        }
        return code;
    }

    public boolean validate(String key, String candidate) {
        String real = store.get(key);
        if (real == null) return false;
        boolean ok = real.equals(candidate);
        if (ok) store.remove(key);
        return ok;
    }
}

