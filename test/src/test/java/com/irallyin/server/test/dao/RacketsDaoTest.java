package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.RacketsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class RacketsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private RacketsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("bag_id", uuid());
        values.put("brand", shortId("v"));
        values.put("model", shortId("v"));
        values.put("grip_size", shortId("v"));
        values.put("weight_gram", 1);
        values.put("head_size_sq_in", 1);
        values.put("image_url", shortId("v"));
        values.put("is_primary", 0);
        values.put("display_order", 1);

        assertCrud(dao, "id", id, values, "bag_id", shortId("u"));
    }
}
