package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecentPlaySessionResponse {
    private String id;
    private String dateText;
    private String title;
    private String detail;
    private String mood;
}
