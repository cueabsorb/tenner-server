package com.irallyin.server.web.controller.admin;

import com.irallyin.server.common.response.ApiResponse;
import com.irallyin.server.core.admin.dto.AdminCourtReviewResponse;
import com.irallyin.server.core.admin.dto.AdminCourtReviewUpdateRequest;
import com.irallyin.server.core.admin.service.AdminCourtReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courts")
@RequiredArgsConstructor
public class AdminCourtReviewController {

    private final AdminCourtReviewService adminCourtReviewService;

    @GetMapping
    public ApiResponse<List<AdminCourtReviewResponse>> listCourts(
            @RequestParam(defaultValue = "pending") String status
    ) {
        return ApiResponse.success(adminCourtReviewService.listCourts(status));
    }

    @PatchMapping("/{courtId}/review")
    public ApiResponse<AdminCourtReviewResponse> updateCourtReview(
            @PathVariable String courtId,
            @Valid @RequestBody AdminCourtReviewUpdateRequest request,
            HttpServletRequest servletRequest
    ) {
        String adminEmail = (String) servletRequest.getAttribute("adminEmail");
        return ApiResponse.success(adminCourtReviewService.updateCourtStatus(courtId, request, adminEmail));
    }
}
