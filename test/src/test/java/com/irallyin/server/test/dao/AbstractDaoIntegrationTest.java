package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.AbstractStatusDao;
import com.irallyin.server.web.IRallyInWebApplication;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest(classes = IRallyInWebApplication.class)
public abstract class AbstractDaoIntegrationTest {

    protected void assertCrud(AbstractStatusDao dao, String idColumn, Object id,
                              Map<String, Object> insertValues,
                              String updateColumn, Object updateValue) {
        Assertions.assertEquals(1, dao.insert(insertValues));

        Optional<Map<String, Object>> inserted = dao.findById(id);
        Assertions.assertTrue(inserted.isPresent(), "inserted row should be found by " + idColumn);
        Assertions.assertEquals(0, statusValue(inserted.get()));

        Map<String, Object> updateValues = new LinkedHashMap<>();
        updateValues.put(updateColumn, updateValue);
        Assertions.assertEquals(1, dao.updateById(id, updateValues));

        Optional<Map<String, Object>> updated = dao.findById(id);
        Assertions.assertTrue(updated.isPresent(), "updated row should still be active");
        Assertions.assertEquals(updateValue, updated.get().get(updateColumn));

        Assertions.assertEquals(1, dao.deleteById(id));
        Assertions.assertTrue(dao.findById(id).isEmpty(), "deleted row should be hidden by status = 0 filter");
    }

    protected static String uuid() {
        return UUID.randomUUID().toString();
    }

    protected static String shortId(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private int statusValue(Map<String, Object> row) {
        Object value = row.get("status");
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
}
