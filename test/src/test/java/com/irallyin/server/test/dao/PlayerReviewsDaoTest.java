package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.PlayerReviewsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class PlayerReviewsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private PlayerReviewsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("play_session_id", uuid());
        values.put("reviewer_id", uuid());
        values.put("reviewee_id", uuid());
        values.put("skill_score", 1);
        values.put("sportsmanship_score", 1);
        values.put("reliability_score", 1);
        values.put("communication_score", 1);
        values.put("overall_score", new BigDecimal("3.5"));
        values.put("tags", "[]");
        values.put("private_note", shortId("v"));
        values.put("moderation_status", "active");
        values.put("is_verified", 0);
        values.put("created_at", LocalDateTime.now());
        values.put("updated_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "play_session_id", shortId("u"));
    }
}
