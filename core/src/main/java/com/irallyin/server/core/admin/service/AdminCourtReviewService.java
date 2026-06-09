package com.irallyin.server.core.admin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irallyin.server.common.exception.BusinessException;
import com.irallyin.server.core.admin.dto.AdminCourtReviewResponse;
import com.irallyin.server.core.admin.dto.AdminCourtReviewUpdateRequest;
import com.irallyin.server.data.mapper.AdminCourtReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminCourtReviewService {

    private final AdminCourtReviewMapper adminCourtReviewMapper;
    private final ObjectMapper objectMapper;

    public List<AdminCourtReviewResponse> listCourts(String status) {
        return adminCourtReviewMapper.findCourts(normalizeStatus(status))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AdminCourtReviewResponse updateCourtStatus(
            String courtId,
            AdminCourtReviewUpdateRequest request,
            String adminEmail
    ) {
        String approvalStatus = normalizeStatus(request.getApprovalStatus());
        String venueStatus = switch (approvalStatus) {
            case "approved" -> "active";
            case "pending" -> "pending_review";
            case "voided", "blacklisted" -> "inactive";
            default -> throw new BusinessException(10001, "审核状态不合法");
        };

        Map<String, Object> pendingChangeRequest = adminCourtReviewMapper.findPendingChangeRequestByCourtId(courtId);
        if (pendingChangeRequest != null) {
            String requestId = stringValue(pendingChangeRequest, "id");
            if ("approved".equals(approvalStatus)) {
                int applied = adminCourtReviewMapper.applyCourtChangeRequest(requestId);
                if (applied == 0) {
                    throw new BusinessException(10004, "修改申请不存在");
                }
            }
            int reviewed = adminCourtReviewMapper.updateCourtChangeRequestReviewStatus(
                    requestId,
                    approvalStatus,
                    adminEmail,
                    request.getReason()
            );
            if (reviewed == 0) {
                throw new BusinessException(10004, "修改申请不存在");
            }
            return toResponse(adminCourtReviewMapper.findCourtById(courtId));
        }

        int updated = adminCourtReviewMapper.updateCourtReviewStatus(
                courtId,
                approvalStatus,
                venueStatus,
                adminEmail,
                request.getReason()
        );
        if (updated == 0) {
            throw new BusinessException(10004, "网球场不存在");
        }
        return toResponse(adminCourtReviewMapper.findCourtById(courtId));
    }

    private String normalizeStatus(String status) {
        return StringUtils.hasText(status) ? status.trim() : "pending";
    }

    private AdminCourtReviewResponse toResponse(Map<String, Object> row) {
        if (row == null) {
            throw new BusinessException(10004, "网球场不存在");
        }
        String approvalStatus = stringValue(row, "approval_status");
        return AdminCourtReviewResponse.builder()
                .id(stringValue(row, "id"))
                .name(stringValue(row, "name"))
                .address(stringValue(row, "address"))
                .country(stringValue(row, "country"))
                .province(stringValue(row, "province"))
                .city(stringValue(row, "city"))
                .approvalStatus(approvalStatus)
                .approvalStatusText(statusText(approvalStatus))
                .venueStatus(stringValue(row, "venue_status"))
                .submitterId(stringValue(row, "created_by"))
                .submitterName(stringValue(row, "submitter_name"))
                .submitterEmail(stringValue(row, "submitter_email"))
                .submitterPhone(stringValue(row, "submitter_phone"))
                .photoUrls(parsePhotoUrls(row.get("photo_urls")))
                .wechatMiniProgramName(stringValue(row, "wechat_mini_program_name"))
                .description(stringValue(row, "description"))
                .reviewedBy(stringValue(row, "reviewed_by"))
                .reviewedAt(localDateTimeValue(row.get("reviewed_at")))
                .rejectedReason(stringValue(row, "rejected_reason"))
                .build();
    }

    private List<String> parsePhotoUrls(Object value) {
        if (value == null) {
            return Collections.emptyList();
        }
        try {
            if (value instanceof String text) {
                if (!StringUtils.hasText(text)) {
                    return Collections.emptyList();
                }
                return objectMapper.readValue(text, new TypeReference<>() {});
            }
            return objectMapper.convertValue(value, new TypeReference<>() {});
        } catch (IllegalArgumentException | java.io.IOException e) {
            return Collections.emptyList();
        }
    }

    private String statusText(String status) {
        return switch (status) {
            case "pending" -> "审核中";
            case "approved" -> "通过";
            case "voided" -> "作废";
            case "blacklisted" -> "黑名单";
            case "rejected" -> "作废";
            default -> status;
        };
    }

    private String stringValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private LocalDateTime localDateTimeValue(Object value) {
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        return null;
    }
}
