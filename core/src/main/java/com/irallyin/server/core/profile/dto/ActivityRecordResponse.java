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
    private List<FeedMetricResponse> metrics;
    private Integer badges;
    private Integer likeCount;

    @Data
    @Builder
    public static class FeedMetricResponse {
        private String label;
        private String value;
    }
}
