package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.UsersDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class UsersDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private UsersDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("phone", "+861" + System.nanoTime());
        values.put("email", "test-" + shortId("mail") + "@example.com");
        values.put("password_hash", shortId("v"));
        values.put("display_name", shortId("v"));
        values.put("avatar_url", shortId("v"));
        values.put("country", "CN");
        values.put("city", shortId("v"));
        values.put("locale", "zh-CN");
        values.put("timezone", "Asia/Shanghai");
        values.put("onboarding_completed", 0);
        values.put("onboarding_step", 1);
        values.put("player_identity", "amateur");
        values.put("ntrp_rating", new BigDecimal("3.5"));
        values.put("sys_ntrp_rating", new BigDecimal("3.5"));
        values.put("access_status", 1);
        values.put("created_at", LocalDateTime.now());
        values.put("updated_at", LocalDateTime.now());
        values.put("deleted_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "phone", shortId("u"));
    }

    @Test
    void shouldQueryByEmail() {
        String id = uuid();
        String email = "test-" + shortId("mail") + "@example.com";
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("phone", "+861" + System.nanoTime());
        values.put("email", email);
        values.put("password_hash", shortId("v"));
        values.put("display_name", shortId("v"));
        values.put("avatar_url", shortId("v"));
        values.put("country", "CN");
        values.put("city", shortId("v"));
        values.put("locale", "zh-CN");
        values.put("timezone", "Asia/Shanghai");
        values.put("onboarding_completed", 0);
        values.put("onboarding_step", 1);
        values.put("player_identity", "amateur");
        values.put("ntrp_rating", new BigDecimal("3.5"));
        values.put("sys_ntrp_rating", new BigDecimal("3.5"));
        values.put("access_status", 1);
        values.put("created_at", LocalDateTime.now());
        values.put("updated_at", LocalDateTime.now());
        values.put("deleted_at", LocalDateTime.now());

        dao.insert(values);

        var results = dao.findByEmail(email);
        Assertions.assertFalse(results.isEmpty(), "should find user by email");
        Assertions.assertEquals(email, results.get(0).get("email"), "email should match");
        Assertions.assertEquals(id, results.get(0).get("id"), "id should match");

        // 查询不存在的邮箱应返回空列表
        var notFound = dao.findByEmail("nonexistent@example.com");
        Assertions.assertTrue(notFound.isEmpty(), "should not find user with non-existent email");
    }
}
