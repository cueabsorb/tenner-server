package com.irallyin.server.core.auth.service;

import com.irallyin.server.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationSender {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("iRallyIn 验证码");
        message.setText("""
                您的 iRallyIn 验证码是：%s

                验证码 5 分钟内有效，请勿泄露给他人。
                """.formatted(code));
        try {
            mailSender.send(message);
        } catch (MailException e) {
            String clientMessage = "验证码邮件发送失败：邮件服务连接或邮箱配置异常";
            log.warn("Verification email send failed: to={}, rawError={}({}) -> clientMessage={}",
                    maskEmail(toEmail), e.getClass().getSimpleName(), e.getMessage(), clientMessage, e);
            throw new BusinessException(10020, clientMessage);
        }
    }

    private String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "";
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return "***" + (atIndex >= 0 ? email.substring(atIndex) : "");
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}
