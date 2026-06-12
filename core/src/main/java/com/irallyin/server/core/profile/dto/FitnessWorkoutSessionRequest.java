package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FitnessWorkoutSessionRequest {
    @NotBlank(message = "训练UUID不能为空")
    @Size(max = 80, message = "训练UUID过长")
    private String healthkitUuid;

    @NotBlank(message = "运动类型不能为空")
    @Size(max = 60, message = "运动类型过长")
    private String sportType;

    @NotBlank(message = "训练开始时间不能为空")
    private String startedAt;

    private String endedAt;
    private Integer durationSeconds;
    private Double activeEnergyKcal;
    private Double basalEnergyKcal;
    private Double totalEnergyKcal;
    private Double distanceMeters;
    private Double avgHeartRate;
    private Double maxHeartRate;
    private Double paceSecondsPerKm;
    private Double speedMps;
    private Double elevationGainMeters;
    private Double cadence;
    private Integer strokeCount;

    @Size(max = 100, message = "来源名称过长")
    private String sourceName;

    @Size(max = 200, message = "来源标识过长")
    private String sourceBundleId;

    @Size(max = 100, message = "设备型号过长")
    private String deviceModel;

    @Size(max = 2000, message = "训练备注过长")
    private String notes;
}
