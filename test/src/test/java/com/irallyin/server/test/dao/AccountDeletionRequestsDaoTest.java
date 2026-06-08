package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.AccountDeletionRequestsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class AccountDeletionRequestsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private AccountDeletionRequestsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("user_id", uuid());
        values.put("reason", shortId("v"));
        values.put("request_status", "pending");
        values.put("created_at", LocalDateTime.now());
        values.put("processed_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "user_id", shortId("u"));
    }
}
