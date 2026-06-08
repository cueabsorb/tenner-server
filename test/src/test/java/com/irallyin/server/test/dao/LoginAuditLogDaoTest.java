package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.LoginAuditLogDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class LoginAuditLogDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private LoginAuditLogDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("user_id", uuid());
        values.put("provider", shortId("v"));
        values.put("ip_address", shortId("v"));
        values.put("device_info", shortId("v"));
        values.put("success", 0);
        values.put("created_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "user_id", shortId("u"));
    }
}
