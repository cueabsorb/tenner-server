package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfilePermissionSettingsUpdateRequest {
    @NotNull(message = "性别显示设置不能为空")
    private Boolean genderVisible;

    @NotNull(message = "生日显示设置不能为空")
    private Boolean birthdayVisible;

    @NotNull(message = "区域显示设置不能为空")
    private Boolean regionVisible;

    @NotNull(message = "常去球场显示设置不能为空")
    private Boolean habitCourtsVisible;

    private Boolean followingListVisible;

    private Boolean followerListVisible;

    private Boolean activityRecordsVisible;
}
