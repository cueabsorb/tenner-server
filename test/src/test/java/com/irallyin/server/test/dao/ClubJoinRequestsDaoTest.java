package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.ClubJoinRequestsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class ClubJoinRequestsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private ClubJoinRequestsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("club_id", uuid());
        values.put("user_id", uuid());
        values.put("message", shortId("v"));
        values.put("request_status", "pending");
        values.put("reviewed_by", uuid());
        values.put("reviewed_at", LocalDateTime.now());
        values.put("created_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "club_id", shortId("u"));
    }
}
