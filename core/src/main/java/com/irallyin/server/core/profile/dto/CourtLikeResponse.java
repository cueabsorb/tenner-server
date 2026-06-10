package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourtLikeResponse {
    private String courtId;
    private Integer likeCount;
    private Boolean likedByMe;
}
