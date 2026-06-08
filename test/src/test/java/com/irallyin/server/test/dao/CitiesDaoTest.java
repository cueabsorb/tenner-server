package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.CitiesDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class CitiesDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private CitiesDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = shortId("K");
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("code", id);
        values.put("name", shortId("v"));
        values.put("english_name", shortId("v"));
        values.put("country_code", "CN");
        values.put("latitude", new BigDecimal("3.5"));
        values.put("longitude", new BigDecimal("3.5"));

        assertCrud(dao, "code", id, values, "name", shortId("u"));
    }
}
