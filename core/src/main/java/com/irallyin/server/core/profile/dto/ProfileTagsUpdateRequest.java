package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProfileTagsUpdateRequest {
    @Size(max = 10, message = "标签最多10个")
    private List<@Size(min = 1, max = 30, message = "标签需为1-30个字符") String> tags;
}
