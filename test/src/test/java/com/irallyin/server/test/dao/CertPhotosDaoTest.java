package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.CertPhotosDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class CertPhotosDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private CertPhotosDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("certification_id", uuid());
        values.put("photo_url", shortId("v"));
        values.put("thumbnail_url", shortId("v"));
        values.put("display_order", 1);

        assertCrud(dao, "id", id, values, "certification_id", shortId("u"));
    }
}
