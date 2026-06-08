package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.ClubsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class ClubsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private ClubsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("name", shortId("v"));
        values.put("english_name", shortId("v"));
        values.put("city_code", shortId("C"));
        values.put("description", shortId("v"));
        values.put("cover_image_url", shortId("v"));
        values.put("owner_id", uuid());
        values.put("join_policy", "open");
        values.put("member_count", 1);
        values.put("club_status", "active");
        values.put("created_at", LocalDateTime.now());
        values.put("updated_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "name", shortId("u"));
    }
}
