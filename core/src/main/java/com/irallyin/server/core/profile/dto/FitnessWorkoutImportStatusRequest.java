package com.irallyin.server.core.profile.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class FitnessWorkoutImportStatusRequest {
    @Size(max = 200, message = "查询数量不能超过200")
    private List<String> healthkitUuids;
}
