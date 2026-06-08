package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileAvatarUpdateRequest {
    @Size(max = 512, message = "头像地址最多512个字符")
    private String avatarUrl;
}
