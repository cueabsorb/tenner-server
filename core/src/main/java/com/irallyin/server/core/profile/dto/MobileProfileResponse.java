package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class MobileProfileResponse {
    private String id;
    private String displayName;
    private String avatarUrl;
    private String intro;
    private String profileNote;
    private List<String> tags;
    private Boolean realNameVisible;
    private String gender;
    private LocalDate birthday;
    private String country;
    private String province;
    private String city;
    private String district;
    private Double ntrpRating;
    private Double sysNtrpRating;
    private String tennisIdentity;
    private String dominantHand;
    private String playPreference;
    private String availabilityText;
    private String acceptedLevelText;
    private String matchPreferenceText;
    private List<HabitCourtResponse> habitCourts;
    private Integer followingCount;
    private Integer followerCount;
    private Integer receivedLikeCount;
}
