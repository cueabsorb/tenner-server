package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.TennisProfileStrengthTagsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class TennisProfileStrengthTagsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private TennisProfileStrengthTagsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("profile_id", uuid());
        values.put("tag", shortId("v"));

        assertCrud(dao, "id", id, values, "profile_id", shortId("u"));
    }
}
