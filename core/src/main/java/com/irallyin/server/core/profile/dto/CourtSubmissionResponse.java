package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourtSubmissionResponse {
    private String courtId;
    private String approvalStatus;
    private String venueStatus;
    private String message;
}
