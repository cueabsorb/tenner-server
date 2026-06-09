package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

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
    private Double latitude;
    private Double longitude;
    private String surfaceType;
    private String indoorOutdoor;
    private Boolean hasIndoor;
    private Boolean hasOutdoor;
    private Integer totalCourtCount;
    private Integer indoorCourtCount;
    private Integer outdoorCourtCount;
    private LocalTime openingTime;
    private LocalTime closingTime;
}
