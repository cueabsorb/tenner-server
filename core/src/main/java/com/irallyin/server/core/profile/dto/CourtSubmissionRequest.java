package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CourtSubmissionRequest {
    @NotBlank(message = "国家不能为空")
    @Size(max = 100, message = "国家不能超过100个字符")
    private String country;

    @NotBlank(message = "城市不能为空")
    @Size(max = 100, message = "城市不能超过100个字符")
    private String city;

    @NotBlank(message = "球场名字不能为空")
    @Size(max = 200, message = "球场名字不能超过200个字符")
    private String name;

    @Size(max = 500, message = "球场地址不能超过500个字符")
    private String address;

    @Size(max = 40, message = "联系方式不能超过40个字符")
    private String contactPhone;

    @Size(max = 100, message = "微信小程序名称不能超过100个字符")
    private String wechatMiniProgramName;

    @Size(max = 5, message = "最多上传5张球场照片")
    private List<@Size(max = 512, message = "照片地址不能超过512个字符") String> photoUrls;

    @Size(max = 1000, message = "信息描述不能超过1000个字符")
    private String description;

    private BigDecimal latitude;
    private BigDecimal longitude;
}
