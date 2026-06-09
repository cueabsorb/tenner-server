package com.irallyin.server.core.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminCourtReviewResponse {
    private String id;
    private String name;
    private String address;
    private String country;
    private String city;
    private String approvalStatus;
    private String approvalStatusText;
    private String venueStatus;
    private String submitterId;
    private String submitterName;
    private String submitterEmail;
    private String submitterPhone;
    private List<String> photoUrls;
    private String wechatMiniProgramName;
    private String description;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String rejectedReason;
}
