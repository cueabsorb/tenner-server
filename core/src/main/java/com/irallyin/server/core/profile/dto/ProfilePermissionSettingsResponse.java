package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfilePermissionSettingsResponse {
    private Boolean genderVisible;
    private Boolean birthdayVisible;
    private Boolean regionVisible;
    private Boolean habitCourtsVisible;
    private Boolean followingListVisible;
    private Boolean followerListVisible;
}
