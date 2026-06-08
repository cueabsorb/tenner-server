package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.FeedMediaDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class FeedMediaDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private FeedMediaDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("post_id", uuid());
        values.put("media_url", shortId("v"));
        values.put("media_type", "image");
        values.put("display_order", 1);
        values.put("thumbnail_url", shortId("v"));

        assertCrud(dao, "id", id, values, "post_id", shortId("u"));
    }
}
