package com.irallyin.server.core.ranking.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RankingSnapshotResponse {
    String updatedAt;
    String nextRefreshAt;
    List<WorldRankingPlayerResponse> men;
    List<WorldRankingPlayerResponse> women;
    List<CityRankingPlayerResponse> city;
}
