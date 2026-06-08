package com.irallyin.server.data.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GenericStatusMapper {
    int insert(
            @Param("tableName") String tableName,
            @Param("entries") List<Map<String, Object>> entries
    );

    int updateById(
            @Param("tableName") String tableName,
            @Param("idColumn") String idColumn,
            @Param("id") Object id,
            @Param("entries") List<Map<String, Object>> entries
    );

    int deleteById(
            @Param("tableName") String tableName,
            @Param("idColumn") String idColumn,
            @Param("id") Object id
    );

    Map<String, Object> findById(
            @Param("tableName") String tableName,
            @Param("idColumn") String idColumn,
            @Param("id") Object id
    );

    List<Map<String, Object>> findByColumn(
            @Param("tableName") String tableName,
            @Param("column") String column,
            @Param("value") Object value
    );
}
