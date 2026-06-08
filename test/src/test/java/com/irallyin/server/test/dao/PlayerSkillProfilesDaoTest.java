package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.PlayerSkillProfilesDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class PlayerSkillProfilesDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private PlayerSkillProfilesDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("user_id", uuid());
        values.put("ntrp_rating", new BigDecimal("3.5"));
        values.put("player_identity", "amateur");
        values.put("confidence_score", new BigDecimal("3.5"));
        values.put("verified_level", shortId("v"));
        values.put("updated_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "user_id", shortId("u"));
    }
}
