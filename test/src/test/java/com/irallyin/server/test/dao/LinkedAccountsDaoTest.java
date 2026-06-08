package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.LinkedAccountsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class LinkedAccountsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private LinkedAccountsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("user_id", uuid());
        values.put("provider", "phone");
        values.put("provider_user_id", uuid());
        values.put("provider_email", "test-" + shortId("mail") + "@example.com");
        values.put("provider_nickname", shortId("v"));
        values.put("provider_avatar_url", shortId("v"));
        values.put("linked_at", LocalDateTime.now());
        values.put("last_login_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "user_id", shortId("u"));
    }
}
