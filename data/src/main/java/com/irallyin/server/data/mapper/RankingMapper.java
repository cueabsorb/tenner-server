package com.irallyin.server.data.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RankingMapper {
    Map<String, Object> findLatestSnapshot();

    List<Map<String, Object>> listWorldPlayers(@Param("snapshotId") String snapshotId);

    List<Map<String, Object>> listCityPlayers(@Param("snapshotId") String snapshotId);
}
