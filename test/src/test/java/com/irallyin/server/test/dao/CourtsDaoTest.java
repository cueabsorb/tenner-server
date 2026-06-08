package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.CourtsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class CourtsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private CourtsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("area_code", shortId("C"));
        values.put("name", shortId("v"));
        values.put("address", shortId("v"));
        values.put("latitude", new BigDecimal("3.5"));
        values.put("longitude", new BigDecimal("3.5"));
        values.put("surface_type", "hard");
        values.put("indoor_outdoor", "indoor");
        values.put("contact_phone", "+861" + System.nanoTime());
        values.put("amenities", "[]");
        values.put("venue_status", "active");

        assertCrud(dao, "id", id, values, "area_code", shortId("u"));
    }
}
