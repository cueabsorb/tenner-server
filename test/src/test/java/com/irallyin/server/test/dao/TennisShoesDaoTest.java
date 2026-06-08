package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.TennisShoesDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class TennisShoesDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private TennisShoesDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("bag_id", uuid());
        values.put("brand", shortId("v"));
        values.put("model", shortId("v"));
        values.put("size", shortId("v"));
        values.put("court_type", "hard");

        assertCrud(dao, "id", id, values, "bag_id", shortId("u"));
    }
}
