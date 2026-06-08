package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.ClubMembersDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class ClubMembersDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private ClubMembersDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("club_id", uuid());
        values.put("user_id", uuid());
        values.put("role", "owner");
        values.put("member_status", "active");
        values.put("joined_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "club_id", shortId("u"));
    }
}
