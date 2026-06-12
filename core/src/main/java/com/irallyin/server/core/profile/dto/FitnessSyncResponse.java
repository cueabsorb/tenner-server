package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FitnessSyncResponse {
    private Integer workoutCount;
    private Integer dailySummaryCount;
}
