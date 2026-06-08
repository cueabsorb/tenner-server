package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.TennisProfileIdolsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class TennisProfileIdolsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private TennisProfileIdolsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("profile_id", uuid());
        values.put("tennis_star_id", uuid());
        values.put("custom_name", shortId("v"));
        values.put("display_order", 1);

        assertCrud(dao, "id", id, values, "profile_id", shortId("u"));
    }
}
