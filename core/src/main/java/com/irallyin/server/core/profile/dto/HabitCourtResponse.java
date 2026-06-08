package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HabitCourtResponse {
    private String id;
    private String name;
    private String address;
    private String country;
    private String city;
    private String contactPhone;
    private String venueStatus;
    private String approvalStatus;
    private Boolean isPrimary;
}
