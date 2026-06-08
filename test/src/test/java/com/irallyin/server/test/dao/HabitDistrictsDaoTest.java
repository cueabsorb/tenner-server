package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.HabitDistrictsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class HabitDistrictsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private HabitDistrictsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("habit_id", uuid());
        values.put("district_code", shortId("C"));

        assertCrud(dao, "id", id, values, "habit_id", shortId("u"));
    }
}
