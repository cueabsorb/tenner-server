package com.irallyin.server.core.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchRequestResponse {
    private String id;
    private String organizerId;
    private String organizerName;
    private String avatarUrl;
    private String title;
    private String courtName;
    private String country;
    private String province;
    private String city;
    private String district;
    private String areaText;
    private String startedAt;
    private String timeText;
    private String matchType;
    private Integer neededPlayers;
    private Double minLevel;
    private Double maxLevel;
    private String levelText;
    private String priceMode;
    private Integer pricePerPerson;
    private String priceText;
    private String note;
    private String distanceText;
}
