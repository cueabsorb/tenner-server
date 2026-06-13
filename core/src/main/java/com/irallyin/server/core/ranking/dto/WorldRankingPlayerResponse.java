package com.irallyin.server.core.ranking.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class WorldRankingPlayerResponse {
    String gender;
    Integer rank;
    String name;
    String country;
    Integer points;
    BigDecimal ntrp;
    String source;
}
