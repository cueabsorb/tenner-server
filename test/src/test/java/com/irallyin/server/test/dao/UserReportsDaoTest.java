package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.UserReportsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class UserReportsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private UserReportsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("reporter_id", uuid());
        values.put("reported_user_id", uuid());
        values.put("report_type", "harassment");
        values.put("description", shortId("v"));
        values.put("resource_type", shortId("v"));
        values.put("resource_id", uuid());
        values.put("review_status", "pending");
        values.put("reviewed_by", uuid());
        values.put("reviewed_at", LocalDateTime.now());
        values.put("created_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "reporter_id", shortId("u"));
    }
}
