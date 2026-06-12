package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MatchRequestCreateRequest {
    @NotBlank(message = "国家不能为空")
    @Size(max = 64, message = "国家不能超过64个字符")
    private String country;

    @Size(max = 64, message = "省份不能超过64个字符")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Size(max = 64, message = "城市不能超过64个字符")
    private String city;

    @Size(max = 64, message = "区县不能超过64个字符")
    private String district;

    @NotBlank(message = "开始时间不能为空")
    private String startedAt;

    @Min(value = 1, message = "活动时长必须大于0分钟")
    @Max(value = 1440, message = "活动时长不能超过24小时")
    private Integer durationMinutes;

    @Size(max = 20, message = "打球方式不能超过20个字符")
    private String matchType;

    @Min(value = 1, message = "缺少人数必须大于0")
    @Max(value = 20, message = "缺少人数不能超过20")
    private Integer neededPlayers;

    private Double minLevel;

    private Double maxLevel;

    @Size(max = 20, message = "费用模式不能超过20个字符")
    private String priceMode;

    @Min(value = 0, message = "费用不能小于0")
    @Max(value = 99999, message = "费用不能超过99999")
    private Integer pricePerPerson;

    @Size(max = 20, message = "性别要求不能超过20个字符")
    private String genderRequirement;

    private String courtId;

    @Size(max = 200, message = "球场名称不能超过200个字符")
    private String courtName;

    @Size(max = 300, message = "备注不能超过300个字符")
    private String note;
}
