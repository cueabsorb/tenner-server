package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.FeedLikesDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class FeedLikesDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private FeedLikesDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("post_id", uuid());
        values.put("user_id", uuid());
        values.put("created_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "post_id", shortId("u"));
    }
}
