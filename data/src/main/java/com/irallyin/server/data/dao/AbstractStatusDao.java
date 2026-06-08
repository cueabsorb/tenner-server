package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class AbstractStatusDao {
    private static final Logger log = LoggerFactory.getLogger(AbstractStatusDao.class);
    private static final Pattern SQL_NAME_PATTERN = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    private final GenericStatusMapper genericStatusMapper;
    private final String tableName;
    private final String idColumn;

    protected AbstractStatusDao(GenericStatusMapper genericStatusMapper, String tableName, String idColumn) {
        this.genericStatusMapper = genericStatusMapper;
        this.tableName = validateQualifiedSqlName(tableName);
        this.idColumn = validateSqlName(idColumn);
    }

    public int insert(Map<String, Object> values) {
        LinkedHashMap<String, Object> insertValues = copyValues(values);
        insertValues.put("status", 0);
        try {
            return genericStatusMapper.insert(tableName, toSqlEntries(insertValues));
        } catch (Exception e) {
            log.error("Failed to insert into {}: {}", tableName, e.getMessage(), e);
            throw e;
        }
    }

    public int updateById(Object id, Map<String, Object> values) {
        requireId(id);
        LinkedHashMap<String, Object> updateValues = copyValues(values);
        updateValues.remove(idColumn);
        updateValues.remove("id");
        if (updateValues.isEmpty()) {
            throw new IllegalArgumentException("update values must not be empty");
        }

        try {
            return genericStatusMapper.updateById(tableName, idColumn, id, toSqlEntries(updateValues));
        } catch (Exception e) {
            log.error("Failed to update {} by {}={}: {}", tableName, idColumn, id, e.getMessage(), e);
            throw e;
        }
    }

    public int deleteById(Object id) {
        requireId(id);
        try {
            return genericStatusMapper.deleteById(tableName, idColumn, id);
        } catch (Exception e) {
            log.error("Failed to soft-delete from {} by {}={}: {}", tableName, idColumn, id, e.getMessage(), e);
            throw e;
        }
    }

    public Optional<Map<String, Object>> findById(Object id) {
        requireId(id);
        try {
            return Optional.ofNullable(genericStatusMapper.findById(tableName, idColumn, id));
        } catch (Exception e) {
            log.error("Failed to find {} by {}={}: {}", tableName, idColumn, id, e.getMessage(), e);
            throw e;
        }
    }

    public List<Map<String, Object>> findByColumn(String column, Object value) {
        String validatedColumn = validateSqlName(column);
        try {
            return genericStatusMapper.findByColumn(tableName, validatedColumn, value);
        } catch (Exception e) {
            log.error("Failed to find {} by {}={}: {}", tableName, validatedColumn, value, e.getMessage(), e);
            throw e;
        }
    }

    private LinkedHashMap<String, Object> copyValues(Map<String, Object> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        LinkedHashMap<String, Object> copied = new LinkedHashMap<>();
        values.forEach((column, value) -> copied.put(validateSqlName(column), value));
        return copied;
    }

    private List<Map<String, Object>> toSqlEntries(LinkedHashMap<String, Object> values) {
        List<Map<String, Object>> entries = new ArrayList<>();
        values.forEach((column, value) -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("column", column);
            entry.put("value", value);
            entries.add(entry);
        });
        return entries;
    }

    private void requireId(Object id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
    }

    private static String validateSqlName(String sqlName) {
        if (sqlName == null || !SQL_NAME_PATTERN.matcher(sqlName).matches()) {
            throw new IllegalArgumentException("invalid SQL name: " + sqlName);
        }
        return sqlName;
    }

    private static String validateQualifiedSqlName(String qualifiedSqlName) {
        if (qualifiedSqlName == null) {
            throw new IllegalArgumentException("invalid SQL name: null");
        }
        String[] parts = qualifiedSqlName.split("\\.");
        if (parts.length < 1 || parts.length > 2) {
            throw new IllegalArgumentException("invalid SQL name: " + qualifiedSqlName);
        }
        for (String part : parts) {
            validateSqlName(part);
        }
        return qualifiedSqlName;
    }
}
