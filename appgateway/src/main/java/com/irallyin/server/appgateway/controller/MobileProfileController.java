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

    @PostMapping("/me")
    @Operation(summary = "获取个人主页编辑资料")
    public ApiResponse<MobileProfileResponse> me(Authentication authentication) {
        try {
            return ApiResponse.success(mobileProfileService.getProfile(currentUserId(authentication)));
        } catch (Exception e) {
            log.error("Failed to get profile: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/permission-settings/detail")
    @Operation(summary = "获取个人主页权限设置")
    public ApiResponse<ProfilePermissionSettingsResponse> getPermissionSettings(Authentication authentication) {
        try {
            return ApiResponse.success(mobileProfileService.getPermissionSettings(currentUserId(authentication)));
        } catch (Exception e) {
            log.error("Failed to get permission settings: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/permission-settings")
    @Operation(summary = "保存个人主页权限设置")
    public ApiResponse<ProfilePermissionSettingsResponse> updatePermissionSettings(
            Authentication authentication,
            @Valid @RequestBody ProfilePermissionSettingsUpdateRequest request
    ) {
        try {
            return ApiResponse.success(mobileProfileService.updatePermissionSettings(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to update permission settings: {}", e.getMessage(), e);
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

    @PostMapping("/habit-courts/search")
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

    @PostMapping("/users/search")
    @Operation(summary = "按名称搜索用户")
    public ApiResponse<List<UserSearchResponse>> searchUsers(
            Authentication authentication,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "false") boolean followingOnly,
            @RequestParam(required = false, defaultValue = "100") Integer limit
    ) {
        try {
            return ApiResponse.success(mobileProfileService.searchUsers(
                    keyword,
                    currentUserId(authentication),
                    followingOnly,
                    limit
            ));
        } catch (Exception e) {
            log.error("Failed to search users: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/users/founder")
    @Operation(summary = "获取 App 开发者账号")
    public ApiResponse<UserSearchResponse> founderUser(Authentication authentication) {
        try {
            return ApiResponse.success(mobileProfileService.getFounderUser(currentUserId(authentication)));
        } catch (Exception e) {
            log.error("Failed to get founder user: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/users/{targetUserId}/follow")
    @Operation(summary = "关注球友")
    public ApiResponse<UserFollowResponse> followUser(Authentication authentication, @PathVariable String targetUserId) {
        try {
            return ApiResponse.success(mobileProfileService.followUser(currentUserId(authentication), targetUserId));
        } catch (Exception e) {
            log.error("Failed to follow user: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/racket-catalog")
    @Operation(summary = "获取球拍基础数据")
    public ApiResponse<List<RacketCatalogResponse>> listRacketCatalog() {
        try {
            return ApiResponse.success(mobileProfileService.listRacketCatalog());
        } catch (Exception e) {
            log.error("Failed to list racket catalog: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/racket-catalog/{catalogId}/player-usages")
    @Operation(summary = "获取职业球员使用球拍记录")
    public ApiResponse<List<RacketPlayerUsageResponse>> listRacketPlayerUsages(@PathVariable String catalogId) {
        try {
            return ApiResponse.success(mobileProfileService.listRacketPlayerUsages(catalogId));
        } catch (Exception e) {
            log.error("Failed to list racket player usages: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/activity-records/list")
    @Operation(summary = "获取精彩记录活动列表")
    public ApiResponse<List<ActivityRecordResponse>> listActivityRecords(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "mine") String scope
    ) {
        try {
            return ApiResponse.success(mobileProfileService.listActivityRecords(currentUserId(authentication), scope));
        } catch (Exception e) {
            log.error("Failed to list activity records: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/activity-records")
    @Operation(summary = "添加精彩记录活动")
    public ApiResponse<ActivityRecordResponse> createActivityRecord(
            Authentication authentication,
            @Valid @RequestBody ActivityRecordCreateRequest request
    ) {
        String userId = currentUserId(authentication);
        try {
            return ApiResponse.success(mobileProfileService.createActivityRecord(userId, request));
        } catch (Exception e) {
            log.error("Failed to create activity record for userId={}, startedAt={}, durationMinutes={}, courtId={}: {}",
                    userId,
                    request.getStartedAt(),
                    request.getDurationMinutes(),
                    request.getCourtId(),
                    e.getMessage(),
                    e);
            throw e;
        }
    }

    @PostMapping("/activity-records/{recordId}/likes")
    @Operation(summary = "点赞精彩记录")
    public ApiResponse<ActivityRecordResponse> likeActivityRecord(
            Authentication authentication,
            @PathVariable String recordId
    ) {
        try {
            return ApiResponse.success(mobileProfileService.likeActivityRecord(currentUserId(authentication), recordId));
        } catch (Exception e) {
            log.error("Failed to like activity record {}: {}", recordId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/fitness/sync")
    @Operation(summary = "同步HealthKit健身数据")
    public ApiResponse<FitnessSyncResponse> syncFitnessData(
            Authentication authentication,
            @Valid @RequestBody FitnessSyncRequest request
    ) {
        String userId = currentUserId(authentication);
        try {
            return ApiResponse.success(mobileProfileService.syncFitnessData(userId, request));
        } catch (Exception e) {
            log.error("Failed to sync fitness data for userId={}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/fitness/workouts/import-status")
    @Operation(summary = "查询HealthKit训练是否已加入精彩记录")
    public ApiResponse<List<FitnessWorkoutImportStatusResponse>> fitnessWorkoutImportStatus(
            Authentication authentication,
            @Valid @RequestBody FitnessWorkoutImportStatusRequest request
    ) {
        try {
            return ApiResponse.success(mobileProfileService.findFitnessWorkoutImportStatuses(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to query fitness workout import statuses: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/fitness/workouts/import")
    @Operation(summary = "将HealthKit训练加入精彩记录")
    public ApiResponse<ActivityRecordResponse> importFitnessWorkout(
            Authentication authentication,
            @Valid @RequestBody FitnessWorkoutSessionRequest request
    ) {
        try {
            return ApiResponse.success(mobileProfileService.importFitnessWorkoutAsActivityRecord(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to import fitness workout as activity record: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/match-requests/search")
    @Operation(summary = "按城市获取约球列表")
    public ApiResponse<List<MatchRequestResponse>> listMatchRequests(
            Authentication authentication,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city
    ) {
        try {
            return ApiResponse.success(mobileProfileService.listMatchRequests(currentUserId(authentication), country, province, city));
        } catch (Exception e) {
            log.error("Failed to list match requests: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/match-requests/list")
    @Operation(summary = "按城市获取约球列表")
    public ApiResponse<List<MatchRequestResponse>> listMatchRequestsByPost(
            Authentication authentication,
            @Valid @RequestBody MatchRequestListRequest request
    ) {
        try {
            return ApiResponse.success(mobileProfileService.listMatchRequests(
                    currentUserId(authentication),
                    request.getCountry(),
                    request.getProvince(),
                    request.getCity()
            ));
        } catch (Exception e) {
            log.error("Failed to list match requests: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/match-requests")
    @Operation(summary = "发布约球")
    public ApiResponse<MatchRequestResponse> createMatchRequest(
            Authentication authentication,
            @Valid @RequestBody MatchRequestCreateRequest request
    ) {
        try {
            return ApiResponse.success(mobileProfileService.createMatchRequest(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to create match request: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/equipment/rackets")
    @Operation(summary = "添加我的球拍装备")
    public ApiResponse<UserRacketAddResponse> addUserRacket(Authentication authentication, @Valid @RequestBody UserRacketAddRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.addUserRacket(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to add user racket: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/admin/racket-catalog")
    @Operation(summary = "管理员添加球拍基础数据")
    public ApiResponse<RacketCatalogResponse> createRacketCatalog(Authentication authentication, @Valid @RequestBody RacketCatalogCreateRequest request) {
        try {
            return ApiResponse.success(mobileProfileService.createRacketCatalog(currentUserId(authentication), request));
        } catch (Exception e) {
            log.error("Failed to create racket catalog: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/courts/{courtId}")
    @Operation(summary = "获取网球场数据库详情")
    public ApiResponse<HabitCourtResponse> getCourt(Authentication authentication, @PathVariable String courtId) {
        try {
            String userId = authentication != null && authentication.getPrincipal() instanceof UUID uuid
                    ? uuid.toString()
                    : null;
            return ApiResponse.success(mobileProfileService.getCourt(userId, courtId));
        } catch (Exception e) {
            log.error("Failed to get court detail: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/courts/{courtId}/likes")
    @Operation(summary = "点赞网球场")
    public ApiResponse<CourtLikeResponse> likeCourt(Authentication authentication, @PathVariable String courtId) {
        try {
            return ApiResponse.success(mobileProfileService.likeCourt(currentUserId(authentication), courtId));
        } catch (Exception e) {
            log.error("Failed to like court: {}", e.getMessage(), e);
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

    @DeleteMapping("/habit-courts/{courtId}")
    @Operation(summary = "删除长期去的网球场")
    public ApiResponse<List<HabitCourtResponse>> removeHabitCourt(Authentication authentication, @PathVariable String courtId) {
        try {
            return ApiResponse.success(mobileProfileService.removeHabitCourt(currentUserId(authentication), courtId));
        } catch (Exception e) {
            log.error("Failed to remove habit court: {}", e.getMessage(), e);
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
