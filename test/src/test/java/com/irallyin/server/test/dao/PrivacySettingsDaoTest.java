package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.PrivacySettingsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class PrivacySettingsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private PrivacySettingsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("user_id", uuid());
        values.put("category", "profile");
        values.put("visibility", "nobody");

        assertCrud(dao, "id", id, values, "user_id", shortId("u"));
    }
}
