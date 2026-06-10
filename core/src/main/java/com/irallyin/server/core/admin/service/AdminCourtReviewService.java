package com.irallyin.server.core.admin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irallyin.server.common.exception.BusinessException;
import com.irallyin.server.core.admin.dto.AdminCourtReviewResponse;
import com.irallyin.server.core.admin.dto.AdminCourtReviewUpdateRequest;
import com.irallyin.server.core.profile.dto.CourtSubmissionRequest;
import com.irallyin.server.data.mapper.AdminCourtReviewMapper;
import com.irallyin.server.data.mapper.MobileProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminCourtReviewService {
    private static final String MOBILE_ADMIN_EMAIL = "tianfengzhang1984@gmail.com";

    private final AdminCourtReviewMapper adminCourtReviewMapper;
    private final MobileProfileMapper mobileProfileMapper;
    private final ObjectMapper objectMapper;

    public List<AdminCourtReviewResponse> listCourts(String status) {
        return adminCourtReviewMapper.findCourts(normalizeStatus(status))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AdminCourtReviewResponse> listMobilePendingCourts(String userId) {
        assertMobileAdmin(userId);
        return listCourts("pending");
    }

    @Transactional
    public AdminCourtReviewResponse updateMobileCourtDraft(String userId, String courtId, CourtSubmissionRequest request) {
        assertMobileAdmin(userId);
        Map<String, Object> values = courtValues(request);
        Map<String, Object> pendingChangeRequest = adminCourtReviewMapper.findPendingChangeRequestByCourtId(courtId);
        int updated = pendingChangeRequest != null
                ? adminCourtReviewMapper.updatePendingChangeRequestDraft(stringValue(pendingChangeRequest, "id"), values)
                : adminCourtReviewMapper.updatePendingCourtDraft(courtId, values);
        if (updated == 0) {
            throw new BusinessException(10004, "待审核网球场不存在");
        }
        return pendingChangeRequest != null
                ? toResponse(adminCourtReviewMapper.findCourts("pending").stream()
                    .filter(row -> courtId.equals(stringValue(row, "id")))
                    .findFirst()
                    .orElse(adminCourtReviewMapper.findCourtById(courtId)))
                : toResponse(adminCourtReviewMapper.findCourtById(courtId));
    }

    @Transactional
    public AdminCourtReviewResponse reviewMobileCourt(String userId, String courtId, AdminCourtReviewUpdateRequest request) {
        assertMobileAdmin(userId);
        String reviewedByUserId = userId;
        String approvalStatus = normalizeStatus(request.getApprovalStatus());
        if ("voided".equals(approvalStatus) || "blacklisted".equals(approvalStatus)) {
            Map<String, Object> pendingChangeRequest = adminCourtReviewMapper.findPendingChangeRequestByCourtId(courtId);
            int deleted = pendingChangeRequest != null
                    ? adminCourtReviewMapper.deletePendingChangeRequest(stringValue(pendingChangeRequest, "id"), reviewedByUserId, request.getReason())
                    : adminCourtReviewMapper.deletePendingCourt(courtId, reviewedByUserId, request.getReason());
            if (deleted == 0) {
                throw new BusinessException(10004, "待审核网球场不存在");
            }
            return AdminCourtReviewResponse.builder()
                    .id(courtId)
                    .approvalStatus("voided")
                    .approvalStatusText(statusText("voided"))
                    .venueStatus("inactive")
                    .reviewedBy(reviewedByUserId)
                    .reviewedAt(LocalDateTime.now())
                    .rejectedReason(request.getReason())
                    .build();
        }
        return updateCourtStatus(courtId, request, reviewedByUserId);
    }

    @Transactional
    public AdminCourtReviewResponse updateCourtStatus(
            String courtId,
            AdminCourtReviewUpdateRequest request,
            String reviewedBy
    ) {
        String reviewedByUserId = resolveReviewedByUserId(reviewedBy);
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
                int applied = adminCourtReviewMapper.applyCourtChangeRequest(requestId, reviewedByUserId);
                if (applied == 0) {
                    throw new BusinessException(10004, "修改申请不存在");
                }
            }
            int reviewed = adminCourtReviewMapper.updateCourtChangeRequestReviewStatus(
                    requestId,
                    approvalStatus,
                    reviewedByUserId,
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
                reviewedByUserId,
                request.getReason()
        );
        if (updated == 0) {
            throw new BusinessException(10004, "网球场不存在");
        }
        return toResponse(adminCourtReviewMapper.findCourtById(courtId));
    }

    private String resolveReviewedByUserId(String reviewedBy) {
        String normalized = normalizeNullable(reviewedBy);
        if (normalized == null) {
            return null;
        }
        if (!normalized.contains("@")) {
            return normalized;
        }
        Map<String, Object> user = mobileProfileMapper.findUserByEmail(normalized);
        String userId = user == null ? null : stringValue(user, "id");
        return StringUtils.hasText(userId) ? userId : normalized;
    }

    private String normalizeStatus(String status) {
        return StringUtils.hasText(status) ? status.trim() : "pending";
    }

    private String assertMobileAdmin(String userId) {
        Map<String, Object> user = mobileProfileMapper.findUserById(userId);
        String email = user == null ? null : stringValue(user, "email");
        if (!MOBILE_ADMIN_EMAIL.equalsIgnoreCase(email)) {
            throw new BusinessException(10003, "无网球场审核权限");
        }
        return email;
    }

    private Map<String, Object> courtValues(CourtSubmissionRequest request) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("country", normalizeRequired(request.getCountry()));
        values.put("province", normalizeRequired(request.getProvince()));
        values.put("city", normalizeRequired(request.getCity()));
        values.put("name", normalizeRequired(request.getName()));
        values.put("address", normalizeNullable(request.getAddress()));
        values.put("contact_phone", normalizeNullable(request.getContactPhone()));
        values.put("wechat_mini_program_name", normalizeNullable(request.getWechatMiniProgramName()));
        values.put("photo_urls", jsonPhotoUrls(request.getPhotoUrls()));
        values.put("description", normalizeNullable(request.getDescription()));
        values.put("latitude", request.getLatitude());
        values.put("longitude", request.getLongitude());
        values.put("map_source", normalizeNullable(request.getMapSource()));
        values.put("surface_type", normalizeNullable(request.getSurfaceType()));
        values.put("has_indoor", request.getHasIndoor());
        values.put("has_outdoor", request.getHasOutdoor());
        values.put("indoor_outdoor", indoorOutdoorValue(request.getHasIndoor(), request.getHasOutdoor()));
        values.put("total_court_count", courtCountValue(request.getTotalCourtCount()));
        values.put("opening_time", timeValue(request.getOpeningTime(), "营业开始时间"));
        values.put("closing_time", timeValue(request.getClosingTime(), "营业结束时间"));
        return values;
    }

    private String jsonPhotoUrls(List<String> photoUrls) {
        List<String> normalized = photoUrls == null ? List.of() : photoUrls.stream()
                .map(this::normalizeNullable)
                .filter(Objects::nonNull)
                .limit(5)
                .toList();
        try {
            return objectMapper.writeValueAsString(normalized);
        } catch (java.io.IOException e) {
            throw new BusinessException(10001, "球场照片格式错误");
        }
    }

    private String normalizeRequired(String value) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            throw new BusinessException(10001, "参数不能为空");
        }
        return normalized;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer courtCountValue(Integer value) {
        if (value == null || value <= 0) {
            return null;
        }
        if (value > 10) {
            throw new BusinessException(10001, "球场片数最多10片");
        }
        return value;
    }

    private LocalTime timeValue(String value, String fieldName) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            return null;
        }
        try {
            return LocalTime.parse(normalized);
        } catch (RuntimeException e) {
            throw new BusinessException(10001, fieldName + "格式不正确");
        }
    }

    private String indoorOutdoorValue(Boolean hasIndoor, Boolean hasOutdoor) {
        if (Boolean.TRUE.equals(hasIndoor) && Boolean.TRUE.equals(hasOutdoor)) {
            return "both";
        }
        if (Boolean.TRUE.equals(hasIndoor)) {
            return "indoor";
        }
        if (Boolean.TRUE.equals(hasOutdoor)) {
            return "outdoor";
        }
        return null;
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
                .contactPhone(stringValue(row, "contact_phone"))
                .photoUrls(parsePhotoUrls(row.get("photo_urls")))
                .wechatMiniProgramName(stringValue(row, "wechat_mini_program_name"))
                .description(stringValue(row, "description"))
                .latitude(doubleValue(row.get("latitude")))
                .longitude(doubleValue(row.get("longitude")))
                .mapSource(stringValue(row, "map_source"))
                .surfaceType(stringValue(row, "surface_type"))
                .indoorOutdoor(stringValue(row, "indoor_outdoor"))
                .hasIndoor(booleanValue(row.get("has_indoor")))
                .hasOutdoor(booleanValue(row.get("has_outdoor")))
                .totalCourtCount(integerValue(row.get("total_court_count")))
                .openingTime(timeStringValue(row.get("opening_time")))
                .closingTime(timeStringValue(row.get("closing_time")))
                .reviewedBy(stringValue(row, "reviewed_by"))
                .reviewerName(stringValue(row, "reviewer_name"))
                .reviewerEmail(stringValue(row, "reviewer_email"))
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

    private Double doubleValue(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return null;
    }

    private Integer integerValue(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return null;
    }

    private Boolean booleanValue(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return null;
    }

    private String timeStringValue(Object value) {
        if (value instanceof LocalTime localTime) {
            return localTime.toString();
        }
        if (value instanceof Time time) {
            return time.toLocalTime().toString();
        }
        return value == null ? null : String.valueOf(value);
    }
}
