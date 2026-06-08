package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.TennisProfilesDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class TennisProfilesDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private TennisProfilesDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("user_id", uuid());
        values.put("play_preference", "singles");
        values.put("backhand_type", "oneHanded");
        values.put("playing_style", "baseliner");
        values.put("created_at", LocalDateTime.now());
        values.put("updated_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "user_id", shortId("u"));
    }
}
