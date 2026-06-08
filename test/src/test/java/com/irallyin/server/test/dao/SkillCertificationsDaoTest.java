package com.irallyin.server.test.dao;

import com.irallyin.server.data.dao.SkillCertificationsDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

class SkillCertificationsDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private SkillCertificationsDao dao;

    @Test
    void shouldInsertUpdateAndLogicDeleteByPrimaryKey() {
        String id = uuid();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", id);
        values.put("user_id", uuid());
        values.put("cert_type", "selfAssessment");
        values.put("cert_status", "draft");
        values.put("requested_ntrp", new BigDecimal("3.5"));
        values.put("final_ntrp", new BigDecimal("3.5"));
        values.put("player_identity", "amateur");
        values.put("coach_id", uuid());
        values.put("coach_comment", shortId("v"));
        values.put("video_analysis_result", "[]");
        values.put("big_data_score", "[]");
        values.put("submitted_at", LocalDateTime.now());
        values.put("verified_at", LocalDateTime.now());
        values.put("expired_at", LocalDateTime.now());
        values.put("rejected_reason", shortId("v"));
        values.put("created_at", LocalDateTime.now());
        values.put("updated_at", LocalDateTime.now());

        assertCrud(dao, "id", id, values, "user_id", shortId("u"));
    }
}
