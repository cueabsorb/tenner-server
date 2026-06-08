package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.PlayingHabitsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class PlayingHabitsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private PlayingHabitsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("user_id", uuid());
        values.put("monthly_freq_min", 1);
        values.put("monthly_freq_max", 1);
        values.put("court_surface_pref", "hard");
        values.put("indoor_outdoor_pref", "indoor");
        values.put("play_preference", "singles");
        values.put("created_at", LocalDateTime.now());
        values.put("updated_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "user_id", shortId("u"));
    }
}
