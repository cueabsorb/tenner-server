package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserSearchResponse {
    private String id;
    private String displayName;
    private String avatarUrl;
    private String gender;
    private Double ntrpRating;
    private String region;
    private List<String> habitCourts;
    private Integer followingCount;
    private Integer followerCount;
    private Boolean isFollowing;
}
