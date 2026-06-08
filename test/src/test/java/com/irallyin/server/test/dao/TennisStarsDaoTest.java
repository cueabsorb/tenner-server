package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.TennisStarsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class TennisStarsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private TennisStarsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("name", shortId("v"));
        values.put("country", "CN");
        values.put("avatar_url", shortId("v"));
        values.put("active_status", "active");
        values.put("handedness", "right");

        assertCrud(dao, "id", id, values, "name", shortId("u"));
    }
}
