package com.irallyin.server.test.integration;

import com.irallyin.server.core.profile.dto.ActivityRecordResponse;
import com.irallyin.server.core.profile.dto.FitnessWorkoutSessionRequest;
import com.irallyin.server.core.profile.service.MobileProfileService;
import com.irallyin.server.web.IRallyInWebApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = IRallyInWebApplication.class)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "logging.level.com.irallyin.server=INFO"
})
@EnabledIfEnvironmentVariable(named = "IRALLYIN_DB_INTEGRATION_TEST", matches = "true")
class HealthKitFitnessImportDatabaseIntegrationTest {

    @Autowired
    private MobileProfileService mobileProfileService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String userId = UUID.randomUUID().toString();
    private final String healthkitUuid = UUID.randomUUID().toString();

    @AfterEach
    void cleanUp() {
        tryDelete("DELETE FROM `ir_profile`.`profile_edit_audit_logs` WHERE `user_id` = ?", userId);
        tryDelete("DELETE FROM `ir_activity`.`play_sessions` WHERE `owner_id` = ? AND `healthkit_uuid` = ?", userId, healthkitUuid);
        tryDelete("DELETE FROM `ir_fitness`.`fitness_workout_sessions` WHERE `user_id` = ? AND `healthkit_uuid` = ?", userId, healthkitUuid);
        tryDelete("DELETE FROM `ir_auth`.`users` WHERE `id` = ?", userId);
    }

    @Test
    void shouldImportHealthKitWalkingWorkoutIntoFitnessAndActivityTables() {
        insertTestUser();

        FitnessWorkoutSessionRequest request = new FitnessWorkoutSessionRequest();
        request.setHealthkitUuid(healthkitUuid);
        request.setSportType("walking");
        request.setStartedAt("2026-06-13T08:20:00.000Z");
        request.setEndedAt("2026-06-13T08:22:00.000Z");
        request.setDurationSeconds(120);
        request.setActiveEnergyKcal(4.0);
        request.setTotalEnergyKcal(4.0);
        request.setDistanceMeters(161.0);
        request.setSourceName("Fitness");
        request.setSourceBundleId("com.apple.Fitness");
        request.setDeviceModel("iPhone");

        ActivityRecordResponse response = mobileProfileService.importFitnessWorkoutAsActivityRecord(userId, request);

        assertNotNull(response);
        assertNotNull(response.getId(), "import should return an activity record id");

        if (schemaExists("ir_fitness")) {
            Integer fitnessRows = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM `ir_fitness`.`fitness_workout_sessions` WHERE `user_id` = ? AND `healthkit_uuid` = ? AND `sport_type` = 'walking'",
                    Integer.class,
                    userId,
                    healthkitUuid
            );
            assertEquals(1, fitnessRows, "HealthKit raw workout should be stored in ir_fitness.fitness_workout_sessions");
        }

        Integer activityRows = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM `ir_activity`.`play_sessions` WHERE `owner_id` = ? AND `healthkit_uuid` = ? AND `status` = 0",
                Integer.class,
                userId,
                healthkitUuid
        );
        assertEquals(1, activityRows, "HealthKit workout should be imported into ir_activity.play_sessions");

        String title = jdbcTemplate.queryForObject(
                "SELECT `title` FROM `ir_activity`.`play_sessions` WHERE `owner_id` = ? AND `healthkit_uuid` = ? LIMIT 1",
                String.class,
                userId,
                healthkitUuid
        );
        assertTrue(title != null && title.contains("步行"), "activity record title should keep the walking sport label");
    }

    private void insertTestUser() {
        jdbcTemplate.update("""
                INSERT INTO `ir_auth`.`users`
                (`id`, `email`, `display_name`, `locale`, `timezone`, `onboarding_completed`, `onboarding_step`, `access_status`, `status`)
                VALUES (?, ?, ?, 'zh-CN', 'Asia/Shanghai', 1, 5, 0, 0)
                """,
                userId,
                "healthkit-import-test-" + healthkitUuid + "@example.invalid",
                "HealthKit导入测试"
        );
    }

    private boolean schemaExists(String schemaName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?",
                Integer.class,
                schemaName
        );
        return count != null && count > 0;
    }

    private void tryDelete(String sql, Object... args) {
        try {
            jdbcTemplate.update(sql, args);
        } catch (Exception ignored) {
            // Keep the original test failure visible if cleanup hits an older schema.
        }
    }
}
