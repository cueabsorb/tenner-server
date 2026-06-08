package com.irallyin.server.core.profile.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileBirthdayUpdateRequest {
    private LocalDate birthday;
}
