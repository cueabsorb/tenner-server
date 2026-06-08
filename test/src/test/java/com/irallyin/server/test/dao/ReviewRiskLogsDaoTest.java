package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.ReviewRiskLogsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class ReviewRiskLogsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private ReviewRiskLogsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("review_id", uuid());
        values.put("risk_type", shortId("v"));
        values.put("risk_detail", "[]");
        values.put("created_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "review_id", shortId("u"));
    }
}
