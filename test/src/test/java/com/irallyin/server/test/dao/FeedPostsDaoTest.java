package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.FeedPostsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class FeedPostsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private FeedPostsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("author_id", uuid());
        values.put("text", shortId("v"));
        values.put("image_urls", "[]");
        values.put("play_session_id", uuid());
        values.put("court_id", uuid());
        values.put("topic_tags", "[]");
        values.put("visibility", "nobody");
        values.put("like_count", 1);
        values.put("comment_count", 1);
        values.put("created_at", LocalDateTime.now());
        values.put("updated_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "author_id", shortId("u"));
    }
}
