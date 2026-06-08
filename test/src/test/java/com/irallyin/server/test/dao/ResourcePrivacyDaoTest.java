package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.ResourcePrivacyDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class ResourcePrivacyDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private ResourcePrivacyDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("owner_id", uuid());
        values.put("resource_type", shortId("v"));
        values.put("resource_id", uuid());
        values.put("visibility", "nobody");
        values.put("allowed_club_ids", "[]");
        values.put("allowed_user_ids", "[]");

        assertCrud(dao, "id", id, values, "owner_id", shortId("u"));
    }
}
