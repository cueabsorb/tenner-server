package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFollowResponse {
    private String userId;
    private String targetUserId;
    private Boolean followed;
    private Integer followingCount;
}
