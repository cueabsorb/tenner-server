package com.irallyin.server.core.ranking.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CityRankingPlayerResponse {
    Integer rank;
    String name;
    String city;
    String level;
    String ntrp;
    BigDecimal utr;
    Integer matches;
    Integer groupWins;
    String avatarUrl;
}
