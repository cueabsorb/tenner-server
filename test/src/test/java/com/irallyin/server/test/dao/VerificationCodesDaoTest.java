package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.VerificationCodesDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class VerificationCodesDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private VerificationCodesDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("target", shortId("v"));
        values.put("code_hash", shortId("v"));
        values.put("purpose", "register");
        values.put("expires_at", LocalDateTime.now());
        values.put("used_at", LocalDateTime.now());
        values.put("created_at", LocalDateTime.now());
        values.put("ip_address", shortId("v"));

        assertCrud(dao, "id", id, values, "target", shortId("u"));
    }
}
