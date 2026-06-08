package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.SkillVideosDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class SkillVideosDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private SkillVideosDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("certification_id", uuid());
        values.put("video_url", shortId("v"));
        values.put("thumbnail_url", shortId("v"));
        values.put("duration_seconds", 1);
        values.put("file_size_mb", new BigDecimal("3.5"));
        values.put("created_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "certification_id", shortId("u"));
    }
}
