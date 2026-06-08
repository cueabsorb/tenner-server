package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.FeedCommentsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class FeedCommentsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private FeedCommentsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("post_id", uuid());
        values.put("author_id", uuid());
        values.put("content", shortId("v"));
        values.put("parent_comment_id", uuid());
        values.put("created_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "post_id", shortId("u"));
    }
}
