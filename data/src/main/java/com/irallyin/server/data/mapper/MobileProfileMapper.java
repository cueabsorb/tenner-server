package com.irallyin.server.data.mapper;

import com.irallyin.server.data.domain.CourtDO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MobileProfileMapper {
    Map<String, Object> findActiveUserById(@Param("userId") String userId);

    Map<String, Object> findUserById(@Param("userId") String userId);

    Map<String, Object> findUserByEmail(@Param("email") String email);

    int updateUser(@Param("userId") String userId, @Param("values") Map<String, Object> values);

    Map<String, Object> findProfilePermissionSettings(@Param("userId") String userId);

    int upsertProfilePermissionSettings(
            @Param("id") String id,
            @Param("userId") String userId,
            @Param("values") Map<String, Object> values
    );

    int upsertFitnessSyncSource(@Param("id") String id, @Param("userId") String userId, @Param("values") Map<String, Object> values);

    int upsertFitnessWorkoutSession(@Param("id") String id, @Param("userId") String userId, @Param("values") Map<String, Object> values);

    int upsertFitnessDailySummary(@Param("id") String id, @Param("userId") String userId, @Param("values") Map<String, Object> values);

    Map<String, Object> findActiveTennisProfileByUserId(@Param("userId") String userId);

    int insertTennisProfile(@Param("id") String id, @Param("userId") String userId);

    int updateTennisProfile(@Param("id") String id, @Param("values") Map<String, Object> values);

    Map<String, Object> findActiveSkillProfileByUserId(@Param("userId") String userId);

    int insertSkillProfile(@Param("values") Map<String, Object> values);

    int updateSkillProfile(@Param("id") String id, @Param("values") Map<String, Object> values);

    Map<String, Object> findActivePlayingHabitByUserId(@Param("userId") String userId);

    int insertPlayingHabit(@Param("values") Map<String, Object> values);

    int updatePlayingHabit(@Param("id") String id, @Param("values") Map<String, Object> values);

    List<Map<String, Object>> findHabitCourtsByUserId(@Param("userId") String userId);

    List<Map<String, Object>> findRecentPlaySessionsByUserId(@Param("userId") String userId);

    List<Map<String, Object>> findActivityRecordsByUserId(@Param("userId") String userId);

    List<Map<String, Object>> findVisibleActivityRecordsByFollowing(@Param("userId") String userId);

    Map<String, Object> findPlaySessionById(
            @Param("sessionId") String sessionId,
            @Param("viewerUserId") String viewerUserId
    );

    int insertActivityRecordLikeDetail(
            @Param("id") String id,
            @Param("sessionId") String sessionId,
            @Param("likerUserId") String likerUserId
    );

    int upsertActivityRecordLikeStats(
            @Param("sessionId") String sessionId,
            @Param("ownerId") String ownerId
    );

    List<Map<String, Object>> findRecentActivityRecordLikers(@Param("sessionId") String sessionId);

    List<String> findImportedPlaySessionHealthkitUuids(
            @Param("userId") String userId,
            @Param("healthkitUuids") List<String> healthkitUuids
    );

    Map<String, Object> findPlaySessionByHealthkitUuid(
            @Param("userId") String userId,
            @Param("healthkitUuid") String healthkitUuid
    );

    int insertPlaySession(@Param("values") Map<String, Object> values);

    List<Map<String, Object>> findMatchRequests(
            @Param("country") String country,
            @Param("province") String province,
            @Param("city") String city
    );

    Map<String, Object> findMatchRequestById(@Param("eventId") String eventId);

    int insertMatchRequest(@Param("values") Map<String, Object> values);

    int insertAppMessage(@Param("values") Map<String, Object> values);

    List<Map<String, Object>> findAppMessages(
            @Param("userId") String userId,
            @Param("messageType") String messageType,
            @Param("rowLimit") int rowLimit
    );

    int countUnreadAppMessages(@Param("userId") String userId, @Param("messageType") String messageType);

    List<CourtDO> searchCourts(
            @Param("country") String country,
            @Param("province") String province,
            @Param("city") String city,
            @Param("keyword") String keyword
    );

    List<Map<String, Object>> searchUsers(
            @Param("keyword") String keyword,
            @Param("currentUserId") String currentUserId,
            @Param("followingOnly") boolean followingOnly,
            @Param("rowLimit") int rowLimit
    );

    CourtDO findActiveCourtById(@Param("courtId") String courtId);

    CourtDO findActiveCourtByName(@Param("name") String name);

    int insertCourt(@Param("values") Map<String, Object> values);

    int insertCourtLike(@Param("id") String id, @Param("courtId") String courtId, @Param("userId") String userId);

    int countCourtLikes(@Param("courtId") String courtId);

    int countCourtLikeByUser(@Param("courtId") String courtId, @Param("userId") String userId);

    int countPendingCourtChangeRequest(@Param("courtId") String courtId);

    int insertCourtChangeRequest(@Param("values") Map<String, Object> values);

    int markCourtPendingReviewForChange(@Param("courtId") String courtId);

    int deactivatePrimaryHabitCourts(@Param("habitId") String habitId);

    int upsertHabitCourt(
            @Param("id") String id,
            @Param("habitId") String habitId,
            @Param("courtId") String courtId,
            @Param("isPrimary") boolean isPrimary
    );

    int deactivateHabitCourt(@Param("habitId") String habitId, @Param("courtId") String courtId);

    int deactivateStrengthTags(@Param("profileId") String profileId);

    int upsertStrengthTag(@Param("id") String id, @Param("profileId") String profileId, @Param("tag") String tag);

    List<String> findActiveStrengthTags(@Param("profileId") String profileId);

    List<Map<String, Object>> findAvailabilitySlots(@Param("userId") String userId);

    int countEditLogsSince(
            @Param("userId") String userId,
            @Param("fieldName") String fieldName,
            @Param("since") LocalDateTime since
    );

    int insertEditLog(@Param("id") String id, @Param("userId") String userId, @Param("fieldName") String fieldName);

    int upsertFollowRelationship(
            @Param("id") String id,
            @Param("userId") String userId,
            @Param("targetUserId") String targetUserId
    );

    int countFollowRelationship(@Param("userId") String userId, @Param("targetUserId") String targetUserId);

    int countFollowing(@Param("userId") String userId);

    int countFollowers(@Param("userId") String userId);

    int sumReceivedLikes(@Param("userId") String userId);

    int countRacketsByUserId(@Param("userId") String userId);

    int countShoesByUserId(@Param("userId") String userId);

    Map<String, Object> findEquipmentBagByUserId(@Param("userId") String userId);

    int insertEquipmentBag(@Param("id") String id, @Param("userId") String userId);

    List<Map<String, Object>> listRacketCatalog();

    Map<String, Object> findRacketCatalogById(@Param("catalogId") String catalogId);

    List<Map<String, Object>> listRacketPlayerUsages(@Param("brand") String brand, @Param("model") String model);

    int insertRacketCatalog(@Param("values") Map<String, Object> values);

    int insertUserRacket(@Param("values") Map<String, Object> values);
}
