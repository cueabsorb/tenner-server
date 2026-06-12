package com.irallyin.server.appgateway.controller;

import com.irallyin.server.common.response.ApiResponse;
import com.irallyin.server.core.admin.dto.AdminCourtReviewResponse;
import com.irallyin.server.core.admin.dto.AdminCourtReviewUpdateRequest;
import com.irallyin.server.core.admin.service.AdminCourtReviewService;
import com.irallyin.server.core.profile.dto.CourtSubmissionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping({"/mobile/profile/admin/courts", "/api/mobile/profile/admin/courts"})
@RequiredArgsConstructor
@Tag(name = "Mobile Court Admin", description = "移动端网球场审核")
public class MobileAdminCourtController {
    private final AdminCourtReviewService adminCourtReviewService;

    @RequestMapping(value = "/pending", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "移动端管理员查看待审核网球场")
    public ApiResponse<List<AdminCourtReviewResponse>> listPendingCourtReviews(Authentication authentication) {
        try {
            return ApiResponse.success(adminCourtReviewService.listMobilePendingCourts(currentUserId(authentication)));
        } catch (Exception e) {
            log.error("Failed to list pending court reviews: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/{courtId}/draft")
    @Operation(summary = "移动端管理员直接更改待审核网球场信息")
    public ApiResponse<AdminCourtReviewResponse> updatePendingCourtDraft(
            Authentication authentication,
            @PathVariable String courtId,
            @Valid @RequestBody CourtSubmissionRequest request
    ) {
        try {
            return ApiResponse.success(adminCourtReviewService.updateMobileCourtDraft(currentUserId(authentication), courtId, request));
        } catch (Exception e) {
            log.error("Failed to update pending court draft: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/{courtId}/review")
    @Operation(summary = "移动端管理员审核网球场")
    public ApiResponse<AdminCourtReviewResponse> reviewPendingCourt(
            Authentication authentication,
            @PathVariable String courtId,
            @Valid @RequestBody AdminCourtReviewUpdateRequest request
    ) {
        try {
            return ApiResponse.success(adminCourtReviewService.reviewMobileCourt(currentUserId(authentication), courtId, request));
        } catch (Exception e) {
            log.error("Failed to review pending court: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String currentUserId(Authentication authentication) {
        return ((UUID) authentication.getPrincipal()).toString();
    }
}
