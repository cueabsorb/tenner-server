package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.PlayerReviewAggregatesDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class PlayerReviewAggregatesDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private PlayerReviewAggregatesDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("user_id", uuid());
        values.put("total_review_count", 1);
        values.put("overall_score", new BigDecimal("3.5"));
        values.put("skill_avg", new BigDecimal("3.5"));
        values.put("sportsmanship_avg", new BigDecimal("3.5"));
        values.put("reliability_avg", new BigDecimal("3.5"));
        values.put("communication_avg", new BigDecimal("3.5"));
        values.put("top_positive_tags", "[]");
        values.put("trust_confidence", new BigDecimal("3.5"));
        values.put("updated_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "user_id", shortId("u"));
    }
}
