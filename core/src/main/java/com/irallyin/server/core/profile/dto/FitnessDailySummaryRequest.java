package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FitnessDailySummaryRequest {
    @NotBlank(message = "汇总日期不能为空")
    private String summaryDate;

    private Integer stepCount;
    private Integer flightsClimbed;
    private Double walkingRunningMeters;
    private Double cyclingMeters;
    private Double swimmingMeters;
    private Double basalEnergyKcal;
    private Double activeEnergyKcal;
    private Integer standMinutes;
    private Integer exerciseMinutes;
    private Double activityRingKcal;
    private Integer exerciseRingMinutes;
    private Integer standRingHours;

    @Size(max = 100, message = "来源名称过长")
    private String sourceName;
}
