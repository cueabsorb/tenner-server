package com.irallyin.server.test.service;

import com.irallyin.server.common.exception.BusinessException;
import com.irallyin.server.common.security.JwtTokenProvider;
import com.irallyin.server.core.auth.dto.AuthTokenResponse;
import com.irallyin.server.core.auth.dto.EmailRegisterRequest;
import com.irallyin.server.core.auth.dto.EmailSendCodeRequest;
import com.irallyin.server.core.auth.dto.VerificationCodeResponse;
import com.irallyin.server.core.auth.service.EmailRegistrationService;
import com.irallyin.server.core.auth.service.EmailVerificationSender;
import com.irallyin.server.data.domain.LinkedAccountDO;
import com.irallyin.server.data.domain.UserDO;
import com.irallyin.server.data.mapper.LinkedAccountMapper;
import com.irallyin.server.data.mapper.RefreshTokenMapper;
import com.irallyin.server.data.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailRegistrationServiceTest {

    @Mock private StringRedisTemplate stringRedisTemplate;
    @Mock private EmailVerificationSender emailVerificationSender;
    @Mock private UserMapper userMapper;
    @Mock private LinkedAccountMapper linkedAccountMapper;
    @Mock private RefreshTokenMapper refreshTokenMapper;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private EmailRegistrationService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "accessTokenExpirationMs", 7200000L);
        ReflectionTestUtils.setField(service, "refreshTokenExpirationMs", 604800000L);
    }

    // ==================== sendCode ====================

    @Test
    void sendCode_shouldStoreCodeInRedisAndSendEmail() {
        // given
        EmailSendCodeRequest request = new EmailSendCodeRequest();
        request.setEmail("Test@Example.COM");

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        VerificationCodeResponse response = service.sendCode(request);

        // then
        assertNotNull(response);
        assertEquals("test@example.com", response.getIdentifier());
        assertEquals("register", response.getScene());
        assertEquals(300, response.getExpiresInSeconds());

        // 验证 Redis 存储了验证码
        verify(valueOperations).set(
                eq("verification-code:test@example.com:register"),
                anyString(),
                any()
        );
        // 验证邮件被发送
        verify(emailVerificationSender).sendCode(eq("test@example.com"), anyString());
    }

    @Test
    void sendCode_shouldGenerate6DigitCode() {
        EmailSendCodeRequest request = new EmailSendCodeRequest();
        request.setEmail("user@test.com");

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        doNothing().when(valueOperations).set(anyString(), codeCaptor.capture(), any());

        service.sendCode(request);

        String code = codeCaptor.getValue();
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"), "验证码应为6位数字: " + code);
    }

    // ==================== register ====================

    @Test
    void register_shouldCreateUserAndReturnToken() {
        // given
        EmailRegisterRequest request = new EmailRegisterRequest();
        request.setEmail("newuser@example.com");
        request.setCode("123456");

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("verification-code:newuser@example.com:register"))
                .thenReturn("123456");
        when(linkedAccountMapper.selectOne(any())).thenReturn(null);
        when(userMapper.insert(any(UserDO.class))).thenReturn(1);
        when(linkedAccountMapper.insert(any(LinkedAccountDO.class))).thenReturn(1);
        when(refreshTokenMapper.insert(any(com.irallyin.server.data.domain.RefreshTokenDO.class))).thenReturn(1);
        when(jwtTokenProvider.generateAccessToken(any(UUID.class))).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any(UUID.class))).thenReturn("refresh-token");

        // when
        AuthTokenResponse response = service.register(request, "127.0.0.1");

        // then
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUser());
        assertEquals("newuser", response.getUser().getDisplayName());
        assertEquals("newuser@example.com", response.getUser().getEmail());

        // 验证用户被插入
        ArgumentCaptor<UserDO> userCaptor = ArgumentCaptor.forClass(UserDO.class);
        verify(userMapper).insert(userCaptor.capture());
        UserDO createdUser = userCaptor.getValue();
        assertEquals("newuser@example.com", createdUser.getEmail());
        assertEquals("newuser", createdUser.getDisplayName());
        assertNotNull(createdUser.getId());

        // 验证关联账号被插入
        ArgumentCaptor<LinkedAccountDO> linkedCaptor = ArgumentCaptor.forClass(LinkedAccountDO.class);
        verify(linkedAccountMapper).insert(linkedCaptor.capture());
        LinkedAccountDO linked = linkedCaptor.getValue();
        assertEquals("email", linked.getProvider());
        assertEquals("newuser@example.com", linked.getProviderEmail());
        assertTrue(linked.getProviderEmailVerified());

        // 验证验证码被删除
        verify(stringRedisTemplate).delete("verification-code:newuser@example.com:register");
    }

    @Test
    void register_shouldThrowWhenCodeExpired() {
        EmailRegisterRequest request = new EmailRegisterRequest();
        request.setEmail("user@test.com");
        request.setCode("123456");

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("verification-code:user@test.com:register")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.register(request, "127.0.0.1"));
        assertEquals(10021, ex.getCode());
        assertTrue(ex.getMessage().contains("过期"));
    }

    @Test
    void register_shouldThrowWhenCodeWrong() {
        EmailRegisterRequest request = new EmailRegisterRequest();
        request.setEmail("user@test.com");
        request.setCode("654321");

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("verification-code:user@test.com:register")).thenReturn("123456");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.register(request, "127.0.0.1"));
        assertEquals(10022, ex.getCode());
        assertTrue(ex.getMessage().contains("错误"));
    }

    @Test
    void register_shouldThrowWhenEmailAlreadyRegistered() {
        EmailRegisterRequest request = new EmailRegisterRequest();
        request.setEmail("existing@test.com");
        request.setCode("123456");

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("verification-code:existing@test.com:register")).thenReturn("123456");
        when(linkedAccountMapper.selectOne(any())).thenReturn(new LinkedAccountDO());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.register(request, "127.0.0.1"));
        assertEquals(10030, ex.getCode());
        assertTrue(ex.getMessage().contains("已注册"));
    }

    @Test
    void register_shouldNormalizeEmailToLowerCase() {
        EmailRegisterRequest request = new EmailRegisterRequest();
        request.setEmail("NEWUSER@EXAMPLE.COM");
        request.setCode("123456");

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("verification-code:newuser@example.com:register")).thenReturn("123456");
        when(linkedAccountMapper.selectOne(any())).thenReturn(null);
        when(userMapper.insert(any(UserDO.class))).thenReturn(1);
        when(linkedAccountMapper.insert(any(LinkedAccountDO.class))).thenReturn(1);
        when(refreshTokenMapper.insert(any(com.irallyin.server.data.domain.RefreshTokenDO.class))).thenReturn(1);
        when(jwtTokenProvider.generateAccessToken(any(UUID.class))).thenReturn("at");
        when(jwtTokenProvider.generateRefreshToken(any(UUID.class))).thenReturn("rt");

        AuthTokenResponse response = service.register(request, "127.0.0.1");

        ArgumentCaptor<UserDO> captor = ArgumentCaptor.forClass(UserDO.class);
        verify(userMapper).insert(captor.capture());
        assertEquals("newuser@example.com", captor.getValue().getEmail());
    }
}
