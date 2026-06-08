package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.HabitWeeklySlotsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class HabitWeeklySlotsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private HabitWeeklySlotsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("habit_id", uuid());
        values.put("day_of_week", 1);
        values.put("start_time", LocalTime.of(10, 0));
        values.put("end_time", LocalTime.of(11, 0));

        assertCrud(dao, "id", id, values, "habit_id", shortId("u"));
    }
}
