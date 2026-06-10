package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActivityRecordCreateRequest {
    @NotBlank(message = "开始时间不能为空")
    private String startedAt;

    @Min(value = 1, message = "活动时长必须大于0分钟")
    @Max(value = 1440, message = "活动时长不能超过24小时")
    private Integer durationMinutes;

    @Size(max = 100, message = "球友备注不能超过100个字符")
    private String partnerName;

    @NotBlank(message = "球场不能为空")
    private String courtId;
}
