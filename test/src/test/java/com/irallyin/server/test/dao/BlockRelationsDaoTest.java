package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.BlockRelationsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class BlockRelationsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private BlockRelationsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("blocker_id", uuid());
        values.put("blocked_user_id", uuid());
        values.put("reason", shortId("v"));
        values.put("created_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "blocker_id", shortId("u"));
    }
}
