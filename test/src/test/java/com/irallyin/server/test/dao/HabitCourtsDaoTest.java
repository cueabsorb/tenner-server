package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.HabitCourtsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class HabitCourtsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private HabitCourtsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("habit_id", uuid());
        values.put("court_id", uuid());
        values.put("is_primary", 0);

        assertCrud(dao, "id", id, values, "habit_id", shortId("u"));
    }
}
