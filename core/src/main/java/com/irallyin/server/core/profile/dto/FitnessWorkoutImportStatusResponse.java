package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FitnessWorkoutImportStatusResponse {
    private String healthkitUuid;
    private Boolean imported;
}
