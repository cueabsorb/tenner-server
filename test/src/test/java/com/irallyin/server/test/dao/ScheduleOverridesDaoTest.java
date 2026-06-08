package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.ScheduleOverridesDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class ScheduleOverridesDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private ScheduleOverridesDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("habit_id", uuid());
        values.put("date", LocalDate.now());
        values.put("is_available", 0);
        values.put("note", shortId("v"));

        assertCrud(dao, "id", id, values, "habit_id", shortId("u"));
    }
}
