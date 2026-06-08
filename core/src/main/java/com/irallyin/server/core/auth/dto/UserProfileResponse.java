package com.irallyin.server.core.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private String id;
    private String email;
    private String displayName;
    private String avatarUrl;
    private String intro;
    private String country;
    private String province;
    private String city;
    private String district;
    private Double ntrpRating;
    private Boolean onboardingCompleted;
}
