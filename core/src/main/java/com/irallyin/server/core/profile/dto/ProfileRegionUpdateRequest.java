package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRegionUpdateRequest {
    @NotBlank(message = "国家不能为空")
    @Size(max = 100, message = "国家最多100个字符")
    private String country;

    @Size(max = 100, message = "省最多100个字符")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Size(max = 100, message = "城市最多100个字符")
    private String city;

    @Size(max = 100, message = "区最多100个字符")
    private String district;
}
