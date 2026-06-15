package com.irallyin.server.core.ranking.service;

import com.irallyin.server.core.oss.AliyunOssService;
import com.irallyin.server.core.ranking.dto.CityRankingPlayerResponse;
import com.irallyin.server.core.ranking.dto.RankingSnapshotResponse;
import com.irallyin.server.core.ranking.dto.WorldRankingPlayerResponse;
import com.irallyin.server.data.mapper.RankingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingMapper rankingMapper;
    private final AliyunOssService aliyunOssService;

    public RankingSnapshotResponse getLatestSnapshot() {
        Map<String, Object> snapshot = rankingMapper.findLatestSnapshot();
        if (snapshot == null || snapshot.isEmpty()) {
            return RankingSnapshotResponse.builder()
                    .updatedAt(null)
                    .nextRefreshAt(null)
                    .men(List.of())
                    .women(List.of())
                    .city(List.of())
                    .build();
        }

        String snapshotId = stringValue(snapshot.get("id"));
        List<WorldRankingPlayerResponse> worldPlayers = rankingMapper.listWorldPlayers(snapshotId)
                .stream()
                .map(this::toWorldPlayer)
                .toList();

        return RankingSnapshotResponse.builder()
                .updatedAt(dateText(snapshot.get("updated_at")))
                .nextRefreshAt(dateText(snapshot.get("next_refresh_at")))
                .men(worldPlayers.stream().filter(player -> "men".equals(player.getGender())).toList())
                .women(worldPlayers.stream().filter(player -> "woman".equals(player.getGender())).toList())
                .city(rankingMapper.listCityPlayers(snapshotId).stream().map(this::toCityPlayer).toList())
                .build();
    }

    private WorldRankingPlayerResponse toWorldPlayer(Map<String, Object> row) {
        return WorldRankingPlayerResponse.builder()
                .gender(stringValue(row.get("gender")))
                .rank(intValue(row.get("rank_no")))
                .name(stringValue(row.get("name")))
                .country(stringValue(row.get("country")))
                .points(intValue(row.get("points")))
                .ntrp(decimalValue(row.get("ntrp")))
                .source(stringValue(row.get("source")))
                .avatarUrl(accessibleAvatarUrl(row.get("avatar_url")))
                .build();
    }

    private CityRankingPlayerResponse toCityPlayer(Map<String, Object> row) {
        return CityRankingPlayerResponse.builder()
                .rank(intValue(row.get("rank_no")))
                .name(stringValue(row.get("name")))
                .city(stringValue(row.get("city")))
                .level(stringValue(row.get("level")))
                .ntrp(stringValue(row.get("ntrp")))
                .utr(decimalValue(row.get("utr")))
                .matches(intValue(row.get("matches")))
                .groupWins(intValue(row.get("group_wins")))
                .avatarUrl(accessibleAvatarUrl(row.get("avatar_url")))
                .build();
    }

    private String accessibleAvatarUrl(Object value) {
        String avatarUrl = stringValue(value);
        if (avatarUrl == null || avatarUrl.isBlank()) {
            return null;
        }
        try {
            return aliyunOssService.resolveAccessibleUrl(avatarUrl);
        } catch (RuntimeException ex) {
            return avatarUrl;
        }
    }

    private String dateText(Object value) {
        if (value instanceof LocalDate localDate) {
            return localDate.toString();
        }
        if (value instanceof Date date) {
            return date.toLocalDate().toString();
        }
        return value == null ? null : String.valueOf(value);
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Integer intValue(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value == null) {
            return null;
        }
        return Integer.valueOf(String.valueOf(value));
    }

    private BigDecimal decimalValue(Object value) {
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        if (value == null) {
            return null;
        }
        return new BigDecimal(String.valueOf(value));
    }
}
