package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.TennisStringSetupsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class TennisStringSetupsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private TennisStringSetupsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("racket_id", uuid());
        values.put("main_string_brand", shortId("v"));
        values.put("cross_string_brand", shortId("v"));
        values.put("string_type", "naturalGut");
        values.put("main_tension_lbs", new BigDecimal("3.5"));
        values.put("cross_tension_lbs", new BigDecimal("3.5"));
        values.put("strung_at", LocalDate.now());

        assertCrud(dao, "id", id, values, "racket_id", shortId("u"));
    }
}
