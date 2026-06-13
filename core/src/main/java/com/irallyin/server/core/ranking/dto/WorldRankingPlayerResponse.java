package com.irallyin.server.core.ranking.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WorldRankingPlayerResponse {
    String gender;
    Integer rank;
    String name;
    String country;
    Integer points;
    String source;
}
