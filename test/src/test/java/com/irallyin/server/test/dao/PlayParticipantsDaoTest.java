package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.PlayParticipantsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class PlayParticipantsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private PlayParticipantsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("session_id", uuid());
        values.put("user_id", uuid());
        values.put("display_name", shortId("v"));
        values.put("role", "owner");
        values.put("side", "side_a");
        values.put("ntrp_snapshot", new BigDecimal("3.5"));
        values.put("participant_status", "confirmed");
        values.put("confirmed_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "session_id", shortId("u"));
    }
}
