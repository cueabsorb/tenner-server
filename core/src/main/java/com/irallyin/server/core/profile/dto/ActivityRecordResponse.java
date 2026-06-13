package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ActivityRecordResponse {
    private String id;
    private String author;
    private String avatarUrl;
    private String meta;
    private String location;
    private String title;
    private String body;
    private String startedAt;
    private Integer durationMinutes;
    private String partnerName;
    private List<FeedMetricResponse> metrics;
    private Integer badges;
    private Integer likeCount;
    private Boolean likedByMe;
    private List<ActivityRecordLikerResponse> recentLikers;

    @Data
    @Builder
    public static class FeedMetricResponse {
        private String label;
        private String value;
    }

    @Data
    @Builder
    public static class ActivityRecordLikerResponse {
        private String userId;
        private String displayName;
        private String avatarUrl;
        private Integer likeCount;
    }
}
