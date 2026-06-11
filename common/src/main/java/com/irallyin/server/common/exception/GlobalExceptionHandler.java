package com.irallyin.server.common.exception;

import com.irallyin.server.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("Business exception: {} - {}", e.getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Validation error: {}", message);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.error(10001, message));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication failed: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(10002, "未认证或Token已过期"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(10003, "无权限访问"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String supportedMethods = e.getSupportedHttpMethods() == null
                ? ""
                : e.getSupportedHttpMethods().stream().map(Object::toString).collect(Collectors.joining(", "));
        String message = supportedMethods.isEmpty()
                ? "请求方法不支持"
                : "请求方法不支持，请使用：" + supportedMethods;
        log.warn("Method not supported on {} {}: {}", request.getMethod(), request.getRequestURI(), message);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(10005, message));
    }

    @ExceptionHandler({AsyncRequestNotUsableException.class, ClientAbortException.class})
    public void handleClientAbort(Exception e, HttpServletRequest request) {
        log.debug("Client disconnected before response completed on {} {}: {}",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e, HttpServletRequest request) {
        if (isClientAbort(e)) {
            log.debug("Client disconnected before response completed on {} {}: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    e.getMessage());
            return null;
        }

        String clientMessage = resolveClientMessage(e);
        log.error("Unexpected error on {} {}: rawError={}({}) -> clientMessage={}",
                request.getMethod(),
                request.getRequestURI(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                clientMessage,
                e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, clientMessage));
    }

    private String resolveClientMessage(Throwable error) {
        Throwable root = rootCause(error);
        if (isInstance(error, MailException.class) || isInstance(root, MailException.class)) {
            return "验证码邮件发送失败：邮件服务连接或邮箱配置异常";
        }
        if (isInstance(error, RedisConnectionFailureException.class)
                || isInstance(error, RedisSystemException.class)
                || isInstance(root, RedisConnectionFailureException.class)
                || isInstance(root, RedisSystemException.class)) {
            return "验证码发送失败：验证码缓存服务异常";
        }
        if (isInstance(error, DataAccessException.class) || isInstance(root, DataAccessException.class)) {
            return "服务暂时不可用：数据库访问异常";
        }
        if (isInstance(root, ConnectException.class) || isInstance(root, SocketTimeoutException.class)) {
            return "服务暂时不可用：外部服务连接异常";
        }
        return "服务器内部错误：" + error.getClass().getSimpleName();
    }

    private boolean isClientAbort(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof AsyncRequestNotUsableException
                    || current instanceof ClientAbortException
                    || current instanceof SocketException) {
                return true;
            }
            String message = current.getMessage();
            if (message != null
                    && (message.contains("Connection reset by peer")
                    || message.contains("Broken pipe")
                    || message.contains("ServletOutputStream failed to flush"))) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private Throwable rootCause(Throwable error) {
        Throwable current = error;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private boolean isInstance(Throwable error, Class<?> type) {
        return error != null && type.isAssignableFrom(error.getClass());
    }
}
