package com.irallyin.server.core.auth.service;

public interface PhoneNumberProvider {
    String getMobile(String accessToken);
}
