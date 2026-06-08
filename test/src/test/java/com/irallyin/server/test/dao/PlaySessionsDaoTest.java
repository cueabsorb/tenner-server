package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.PlaySessionsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class PlaySessionsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private PlaySessionsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("owner_id", uuid());
        values.put("sport_type", shortId("v"));
        values.put("session_type", "rally");
        values.put("title", shortId("v"));
        values.put("started_at", LocalDateTime.now());
        values.put("ended_at", LocalDateTime.now());
        values.put("duration_minutes", 1);
        values.put("city_code", shortId("C"));
        values.put("district_code", shortId("C"));
        values.put("court_id", uuid());
        values.put("court_name", shortId("v"));
        values.put("score_summary", "[]");
        values.put("notes", shortId("v"));
        values.put("privacy_level", "nobody");
        values.put("created_at", LocalDateTime.now());
        values.put("updated_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "owner_id", shortId("u"));
    }
}
