package com.irallyin.server.core.auth.service;

import com.irallyin.server.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class AliyunPhoneNumberProvider implements PhoneNumberProvider {

    @Value("${aliyun.phone-auth.mock-enabled:false}")
    private boolean mockEnabled;

    @Value("${aliyun.phone-auth.mock-phone:13800138000}")
    private String mockPhone;

    @Override
    public String getMobile(String accessToken) {
        if (mockEnabled) {
            log.warn("Aliyun phone auth mock is enabled. Returning configured mock phone.");
            return mockPhone;
        }

        if (!StringUtils.hasText(accessToken)) {
            throw new BusinessException(10031, "网关认证Token不能为空");
        }

        // TODO: 接入阿里云号码认证服务 GetMobile。
        // 文档要求服务端使用 App 端 SDK 获取的 AccessToken 调用 Dypnsapi GetMobile，
        // 成功后从返回结构中读取真实手机号。这里先保留单一适配点，避免认证逻辑散落在 Controller。
        throw new BusinessException(10032, "阿里云号码认证尚未配置");
    }
}
