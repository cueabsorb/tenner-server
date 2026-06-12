package com.irallyin.server.core.profile.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class FitnessSyncRequest {
    @Valid
    @Size(max = 200, message = "单次同步训练记录不能超过200条")
    private List<FitnessWorkoutSessionRequest> workouts;

    @Valid
    @Size(max = 90, message = "单次同步每日汇总不能超过90条")
    private List<FitnessDailySummaryRequest> dailySummaries;
}
