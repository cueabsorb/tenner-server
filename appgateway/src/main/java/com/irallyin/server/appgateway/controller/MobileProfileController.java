package com.irallyin.server.appgateway.controller;

import com.irallyin.server.common.response.ApiResponse;
import com.irallyin.server.core.profile.dto.*;
import com.irallyin.server.core.profile.service.MobileProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping({"/mobile/profile", "/api/mobile/profile"})
@RequiredArgsConstructor
@Tag(name = "Mobile Profile", description = "移动端个人主页编辑")
public class MobileProfileController {
    private final MobileProfileService mobileProfileService;

    @GetMapping("/me")
    @Operation(summary = "获取个人主页编辑资料")
    public ApiResponse<MobileProfileResponse> me(Authentication authentication) {
        try {
            return ApiResponse.success(mobileProfileService.getProfile(currentUserId(authentication)));
        } catch (Exception e) {
            log.error("Failed to get profile: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/name")
    @Operation(summary = "编辑名字")
    public ApiResponse<MobileProfileResponse> updateName(Authentication authentication, @Valid @RequestBody ProfileNameUpdateRequest request) {
        try {
            String userId = currentUserId(authentication);
            log.info("Updating mobile profile name for userId={}", userId);
            return ApiResponse.success(mobileProfileService.updateName(userId, request));
        } catch (Exception e) {
            log.error("Failed to update name: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/avatar")
    @Operation(summary = "编辑个人照片")
    public ApiResponse<MobileProfileResponse> updateAvatar(Authentication authentication, @Valid @RequestBody ProfileAvatarUpdateRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.updateAvatar(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to update avatar: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/intro")
    @Operation(summary = "编辑简介")
    public ApiResponse<MobileProfileResponse> updateIntro(Authentication authentication, @Valid @RequestBody ProfileIntroUpdateRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.updateIntro(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to update intro: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/tags")
    @Operation(summary = "编辑标签")
    public ApiResponse<MobileProfileResponse> updateTags(Authentication authentication, @Valid @RequestBody ProfileTagsUpdateRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.updateTags(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to update tags: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/real-name-visibility")
    @Operation(summary = "编辑实名认证展示方式")
    public ApiResponse<MobileProfileResponse> updateRealNameVisibility(Authentication authentication, @Valid @RequestBody ProfileRealNameVisibilityUpdateRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.updateRealNameVisibility(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to update real name visibility: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/gender")
    @Operation(summary = "编辑性别")
    public ApiResponse<MobileProfileResponse> updateGender(Authentication authentication, @Valid @RequestBody ProfileGenderUpdateRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.updateGender(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to update gender: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/birthday")
    @Operation(summary = "编辑生日")
    public ApiResponse<MobileProfileResponse> updateBirthday(Authentication authentication, @Valid @RequestBody ProfileBirthdayUpdateRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.updateBirthday(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to update birthday: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/region")
    @Operation(summary = "编辑区域")
    public ApiResponse<MobileProfileResponse> updateRegion(Authentication authentication, @Valid @RequestBody ProfileRegionUpdateRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.updateRegion(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to update region: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/tennis-level")
    @Operation(summary = "编辑网球水平")
    public ApiResponse<MobileProfileResponse> updateTennisLevel(Authentication authentication, @Valid @RequestBody ProfileTennisLevelUpdateRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.updateTennisLevel(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to update tennis level: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/tennis-identity")
    @Operation(summary = "编辑网球身份")
    public ApiResponse<MobileProfileResponse> updateTennisIdentity(Authentication authentication, @Valid @RequestBody ProfileTennisIdentityUpdateRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.updateTennisIdentity(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to update tennis identity: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/dominant-hand")
    @Operation(summary = "编辑常用手")
    public ApiResponse<MobileProfileResponse> updateDominantHand(Authentication authentication, @Valid @RequestBody ProfileDominantHandUpdateRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.updateDominantHand(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to update dominant hand: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/play-preference")
    @Operation(summary = "编辑打球偏好")
    public ApiResponse<MobileProfileResponse> updatePlayPreference(Authentication authentication, @Valid @RequestBody ProfilePlayPreferenceUpdateRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.updatePlayPreference(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to update play preference: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/habit-courts/search")
    @Operation(summary = "搜索可添加的常去网球场")
    public ApiResponse<List<HabitCourtResponse>> searchHabitCourts(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword
    ) {
        try {
            return ApiResponse.success(mobileProfileService.searchCourts(country, province, city, keyword));
        } catch (Exception e) {
            log.error("Failed to search habit courts: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/users/search")
    @Operation(summary = "按名称搜索用户")
    public ApiResponse<List<UserSearchResponse>> searchUsers(@RequestParam(required = false) String keyword) {
        try {
            return ApiResponse.success(mobileProfileService.searchUsers(keyword));
        } catch (Exception e) {
            log.error("Failed to search users: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/courts/{courtId}")
    @Operation(summary = "获取网球场数据库详情")
    public ApiResponse<HabitCourtResponse> getCourt(@PathVariable String courtId) {
        try {
            return ApiResponse.success(mobileProfileService.getCourt(courtId));
        } catch (Exception e) {
            log.error("Failed to get court detail: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/habit-courts")
    @Operation(summary = "添加长期去的网球场")
    public ApiResponse<List<HabitCourtResponse>> addHabitCourt(Authentication authentication, @Valid @RequestBody HabitCourtAddRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.addHabitCourt(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to add habit court: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/courts/submissions")
    @Operation(summary = "提交新网球场进入审批")
    public ApiResponse<CourtSubmissionResponse> submitCourt(Authentication authentication, @Valid @RequestBody CourtSubmissionRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.submitCourt(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to submit court: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/courts/{courtId}/change-requests")
    @Operation(summary = "提交网球场信息修改申请")
    public ApiResponse<CourtSubmissionResponse> submitCourtChangeRequest(
            Authentication authentication,
            @PathVariable String courtId,
            @Valid @RequestBody CourtSubmissionRequest request
    ) {
        try {
            return ApiResponse.success(mobileProfileService.submitCourtChangeRequest(currentUserId(authentication), courtId, request));
        } catch (Exception e) {
            log.error("Failed to submit court change request: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String currentUserId(Authentication authentication) {
        return ((UUID) authentication.getPrincipal()).toString();
    }
}
