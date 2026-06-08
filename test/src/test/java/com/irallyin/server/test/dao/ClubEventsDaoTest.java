package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.ClubEventsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class ClubEventsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private ClubEventsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("club_id", uuid());
        values.put("organizer_id", uuid());
        values.put("title", shortId("v"));
        values.put("court_id", uuid());
        values.put("court_name", shortId("v"));
        values.put("started_at", LocalDateTime.now());
        values.put("ended_at", LocalDateTime.now());
        values.put("max_participants", 1);
        values.put("event_status", "upcoming");
        values.put("created_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "club_id", shortId("u"));
    }
}
