package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MatchRequestListRequest {
    @Size(max = 64, message = "国家不能超过64个字符")
    private String country;

    @Size(max = 64, message = "省份不能超过64个字符")
    private String province;

    @Size(max = 64, message = "城市不能超过64个字符")
    private String city;
}
