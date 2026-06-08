package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.RefreshTokensDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class RefreshTokensDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private RefreshTokensDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("user_id", uuid());
        values.put("token_hash", shortId("v"));
        values.put("device_id", uuid());
        values.put("device_info", shortId("v"));
        values.put("expires_at", LocalDateTime.now());
        values.put("created_at", LocalDateTime.now());
        values.put("revoked_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "user_id", shortId("u"));
    }
}
