package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.PlayerRelationshipEdgesDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class PlayerRelationshipEdgesDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private PlayerRelationshipEdgesDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("user_id", uuid());
        values.put("peer_user_id", uuid());
        values.put("total_sessions", 1);
        values.put("last_played_at", LocalDateTime.now());
        values.put("most_common_session_type", "rally");
        values.put("relationship_strength", new BigDecimal("3.5"));
        values.put("updated_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "user_id", shortId("u"));
    }
}
