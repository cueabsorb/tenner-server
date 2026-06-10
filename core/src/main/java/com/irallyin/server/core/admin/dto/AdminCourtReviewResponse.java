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
    private String province;
    private String city;
    private String approvalStatus;
    private String approvalStatusText;
    private String venueStatus;
    private String submitterId;
    private String submitterName;
    private String submitterEmail;
    private String submitterPhone;
    private String contactPhone;
    private List<String> photoUrls;
    private String wechatMiniProgramName;
    private String description;
    private Double latitude;
    private Double longitude;
    private String mapSource;
    private String surfaceType;
    private String indoorOutdoor;
    private Boolean hasIndoor;
    private Boolean hasOutdoor;
    private Integer totalCourtCount;
    private String openingTime;
    private String closingTime;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String rejectedReason;
}
