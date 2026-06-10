package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RacketPlayerUsageResponse {
    private String id;
    private String playerName;
    private String brand;
    private String model;
    private Integer usageYear;
    private String notes;
}
