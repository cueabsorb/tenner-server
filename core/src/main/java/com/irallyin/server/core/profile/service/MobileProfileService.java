package com.irallyin.server.core.profile.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irallyin.server.common.cache.RedisKeys;
import com.irallyin.server.common.exception.BusinessException;
import com.irallyin.server.core.profile.dto.*;
import com.irallyin.server.data.domain.CourtDO;
import com.irallyin.server.data.mapper.MobileProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MobileProfileService {
    private static final String DEFAULT_INTRO = "因热爱而相聚，为梦想而挥拍";
    private static final String MOBILE_ADMIN_EMAIL = "tianfengzhang1984@gmail.com";
    private static final String FOUNDER_EMAIL = "656619107@qq.com";
    private static final DateTimeFormatter PROFILE_ACTIVITY_DATE_FORMATTER = DateTimeFormatter.ofPattern("M 月 d 日");
    private static final Set<String> SYSTEM_AVATAR_NAMES = Set.of(
            "female-01", "female-02", "female-03", "female-04", "female-05",
            "female-06", "female-07", "female-08", "female-09", "female-10",
            "male-01", "male-02", "male-03", "male-04", "male-05",
            "male-06", "male-07", "male-08", "male-09", "male-10"
    );

    private final MobileProfileMapper mobileProfileMapper;
    private final ProfileContentSafetyService contentSafetyService;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public MobileProfileResponse getProfile(String userId) {
        Map<String, Object> user = requireUser(userId);
        Optional<Map<String, Object>> tennisProfile = findTennisProfile(userId);
        Optional<Map<String, Object>> skillProfile = findSkillProfile(userId);
        List<String> tags = tennisProfile
                .map(row -> findStrengthTags((String) rowValue(row, "id")))
                .orElseGet(List::of);
        String bio = (String) rowValue(user, "bio");
        String intro = StringUtils.hasText(bio) ? bio : DEFAULT_INTRO;
        Double ntrpRating = numberAsDouble(rowValue(user, "ntrp_rating"), skillProfile.map(row -> rowValue(row, "ntrp_rating")).orElse(null));
        String tennisIdentity = (String) rowValue(user, "player_identity");
        String playPreference = tennisProfile.map(row -> (String) rowValue(row, "play_preference")).orElse(null);

        return MobileProfileResponse.builder()
                .id(userId)
                .displayName((String) rowValue(user, "display_name"))
                .avatarUrl((String) rowValue(user, "avatar_url"))
                .intro(intro)
                .profileNote(buildProfileNote(intro, tags))
                .tags(tags)
                .realNameVisible(booleanValue(rowValue(user, "real_name_visible")))
                .gender((String) rowValue(user, "gender"))
                .birthday(localDateValue(rowValue(user, "birthday")))
                .country((String) rowValue(user, "country"))
                .province((String) rowValue(user, "province"))
                .city((String) rowValue(user, "city"))
                .district((String) rowValue(user, "district"))
                .ntrpRating(ntrpRating)
                .sysNtrpRating(numberAsDouble(rowValue(user, "sys_ntrp_rating"), null))
                .tennisIdentity(tennisIdentity)
                .dominantHand((String) rowValue(user, "dominant_hand"))
                .playPreference(playPreference)
                .availabilityText(buildAvailabilityText(userId))
                .acceptedLevelText(buildAcceptedLevelText(ntrpRating))
                .matchPreferenceText(buildMatchPreferenceText(playPreference, tennisIdentity))
                .recentPlaySessions(findRecentPlaySessions(userId))
                .habitCourts(findHabitCourts(userId))
                .followingCount(getFollowingCount(userId))
                .followerCount(mobileProfileMapper.countFollowers(userId))
                .receivedLikeCount(mobileProfileMapper.sumReceivedLikes(userId))
                .racketCount(mobileProfileMapper.countRacketsByUserId(userId))
                .shoeCount(mobileProfileMapper.countShoesByUserId(userId))
                .build();
    }

    public ProfilePermissionSettingsResponse getPermissionSettings(String userId) {
        requireUser(userId);
        return toPermissionSettingsResponse(mobileProfileMapper.findProfilePermissionSettings(userId));
    }

    @Transactional
    public FitnessSyncResponse syncFitnessData(String userId, FitnessSyncRequest request) {
        requireUser(userId);
        int workoutCount = 0;
        int dailySummaryCount = 0;
        Set<String> syncedSources = new LinkedHashSet<>();

        if (request.getWorkouts() != null) {
            for (FitnessWorkoutSessionRequest workout : request.getWorkouts()) {
                Map<String, Object> values = fitnessWorkoutValues(workout);
                String sourceName = (String) values.get("source_name");
                try {
                    mobileProfileMapper.upsertFitnessWorkoutSession(UUID.randomUUID().toString(), userId, values);
                } catch (DataAccessException e) {
                    logDataAccessFailure("Sync fitness workout DB upsert failed", e,
                            "userId", userId,
                            "healthkitUuid", values.get("healthkit_uuid"),
                            "sportType", values.get("sport_type"),
                            "startedAt", values.get("started_at"),
                            "values", values);
                    throw e;
                }
                workoutCount++;
                if (StringUtils.hasText(sourceName)) {
                    syncedSources.add(sourceName);
                }
            }
        }

        if (request.getDailySummaries() != null) {
            for (FitnessDailySummaryRequest summary : request.getDailySummaries()) {
                Map<String, Object> values = new LinkedHashMap<>();
                String sourceName = normalizeNullable(summary.getSourceName());
                values.put("summary_date", parseFitnessDate(summary.getSummaryDate()));
                values.put("step_count", summary.getStepCount());
                values.put("flights_climbed", summary.getFlightsClimbed());
                values.put("walking_running_meters", summary.getWalkingRunningMeters());
                values.put("cycling_meters", summary.getCyclingMeters());
                values.put("swimming_meters", summary.getSwimmingMeters());
                values.put("basal_energy_kcal", summary.getBasalEnergyKcal());
                values.put("active_energy_kcal", summary.getActiveEnergyKcal());
                values.put("stand_minutes", summary.getStandMinutes());
                values.put("exercise_minutes", summary.getExerciseMinutes());
                values.put("activity_ring_kcal", summary.getActivityRingKcal());
                values.put("exercise_ring_minutes", summary.getExerciseRingMinutes());
                values.put("stand_ring_hours", summary.getStandRingHours());
                values.put("source_name", sourceName);
                values.put("raw_payload", null);
                try {
                    mobileProfileMapper.upsertFitnessDailySummary(UUID.randomUUID().toString(), userId, values);
                } catch (DataAccessException e) {
                    logDataAccessFailure("Sync fitness daily summary DB upsert failed", e,
                            "userId", userId,
                            "summaryDate", values.get("summary_date"),
                            "sourceName", values.get("source_name"),
                            "values", values);
                    throw e;
                }
                dailySummaryCount++;
                if (StringUtils.hasText(sourceName)) {
                    syncedSources.add(sourceName);
                }
            }
        }

        for (String sourceName : syncedSources) {
            Map<String, Object> sourceValues = new LinkedHashMap<>();
            sourceValues.put("source_name", sourceName);
            sourceValues.put("source_bundle_id", "");
            sourceValues.put("device_name", null);
            sourceValues.put("device_model", null);
            sourceValues.put("last_sync_at", LocalDateTime.now());
            sourceValues.put("permission_snapshot", null);
            try {
                mobileProfileMapper.upsertFitnessSyncSource(UUID.randomUUID().toString(), userId, sourceValues);
            } catch (DataAccessException e) {
                logDataAccessFailure("Sync fitness source DB upsert failed", e,
                        "userId", userId,
                        "sourceName", sourceName,
                        "values", sourceValues);
                throw e;
            }
        }

        return FitnessSyncResponse.builder()
                .workoutCount(workoutCount)
                .dailySummaryCount(dailySummaryCount)
                .build();
    }

    @Transactional
    public ProfilePermissionSettingsResponse updatePermissionSettings(String userId, ProfilePermissionSettingsUpdateRequest request) {
        requireUser(userId);
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("gender_visible", Boolean.TRUE.equals(request.getGenderVisible()) ? 1 : 0);
        values.put("birthday_visible", Boolean.TRUE.equals(request.getBirthdayVisible()) ? 1 : 0);
        values.put("region_visible", Boolean.TRUE.equals(request.getRegionVisible()) ? 1 : 0);
        values.put("habit_courts_visible", Boolean.TRUE.equals(request.getHabitCourtsVisible()) ? 1 : 0);
        values.put("following_list_visible", Boolean.TRUE.equals(request.getFollowingListVisible()) ? 1 : 0);
        values.put("follower_list_visible", Boolean.TRUE.equals(request.getFollowerListVisible()) ? 1 : 0);
        values.put("activity_records_visible", Boolean.TRUE.equals(request.getActivityRecordsVisible()) ? 1 : 0);
        mobileProfileMapper.upsertProfilePermissionSettings(UUID.randomUUID().toString(), userId, values);
        writeEditLog(userId, "profile_permission_settings");
        return getPermissionSettings(userId);
    }

    @Transactional
    public MobileProfileResponse updateAvatar(String userId, ProfileAvatarUpdateRequest request) {
        String avatarUrl = normalizeAvatarValue(request.getAvatarUrl());
        updateUser(userId, Map.of("avatar_url", avatarUrl == null ? "" : avatarUrl));
        writeEditLog(userId, "avatar");
        return getProfile(userId);
    }

    @Transactional
    public MobileProfileResponse updateName(String userId, ProfileNameUpdateRequest request) {
        String name = normalize(request.getName());
        contentSafetyService.assertSafeText(name, "名字");
        updateUser(userId, Map.of("display_name", name));
        writeEditLog(userId, "name");
        return getProfile(userId);
    }

    @Transactional
    public MobileProfileResponse updateIntro(String userId, ProfileIntroUpdateRequest request) {
        String intro = normalizeNullable(request.getIntro());
        contentSafetyService.assertSafeText(intro, "简介");
        updateUser(userId, Map.of("bio", intro == null ? "" : intro));
        writeEditLog(userId, "intro");
        return getProfile(userId);
    }

    @Transactional
    public MobileProfileResponse updateTags(String userId, ProfileTagsUpdateRequest request) {
        String profileId = ensureTennisProfile(userId);
        List<String> tags = normalizeTags(request.getTags());
        for (String tag : tags) {
            contentSafetyService.assertSafeText(tag, "标签");
        }
        mobileProfileMapper.deactivateStrengthTags(profileId);
        for (String tag : tags) {
            mobileProfileMapper.upsertStrengthTag(UUID.randomUUID().toString(), profileId, tag);
        }
        writeEditLog(userId, "tags");
        return getProfile(userId);
    }

    @Transactional
    public MobileProfileResponse updateRealNameVisibility(String userId, ProfileRealNameVisibilityUpdateRequest request) {
        updateUser(userId, Map.of("real_name_visible", Boolean.TRUE.equals(request.getVisible()) ? 1 : 0));
        writeEditLog(userId, "real_name_visible");
        return getProfile(userId);
    }

    @Transactional
    public MobileProfileResponse updateGender(String userId, ProfileGenderUpdateRequest request) {
        updateUser(userId, Map.of("gender", request.getGender()));
        writeEditLog(userId, "gender");
        return getProfile(userId);
    }

    @Transactional
    public MobileProfileResponse updateBirthday(String userId, ProfileBirthdayUpdateRequest request) {
        updateUserNullable(userId, "birthday", request.getBirthday());
        writeEditLog(userId, "birthday");
        return getProfile(userId);
    }

    @Transactional
    public MobileProfileResponse updateRegion(String userId, ProfileRegionUpdateRequest request) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("country", normalize(request.getCountry()));
        values.put("province", normalizeNullable(request.getProvince()));
        values.put("city", normalize(request.getCity()));
        values.put("district", normalizeNullable(request.getDistrict()));
        updateUser(userId, values);
        writeEditLog(userId, "region");
        return getProfile(userId);
    }

    @Transactional
    public MobileProfileResponse updateTennisLevel(String userId, ProfileTennisLevelUpdateRequest request) {
        updateUser(userId, Map.of("ntrp_rating", request.getNtrpRating()));
        upsertSkillProfile(userId, Map.of("ntrp_rating", request.getNtrpRating()));
        writeEditLog(userId, "tennis_level");
        return getProfile(userId);
    }

    @Transactional
    public MobileProfileResponse updateTennisIdentity(String userId, ProfileTennisIdentityUpdateRequest request) {
        updateUser(userId, Map.of("player_identity", request.getTennisIdentity()));
        upsertSkillProfile(userId, Map.of("player_identity", request.getTennisIdentity()));
        writeEditLog(userId, "tennis_identity");
        return getProfile(userId);
    }

    @Transactional
    public MobileProfileResponse updateDominantHand(String userId, ProfileDominantHandUpdateRequest request) {
        updateUser(userId, Map.of("dominant_hand", request.getDominantHand()));
        writeEditLog(userId, "dominant_hand");
        return getProfile(userId);
    }

    @Transactional
    public MobileProfileResponse updatePlayPreference(String userId, ProfilePlayPreferenceUpdateRequest request) {
        upsertTennisProfile(userId, Map.of("play_preference", request.getPlayPreference()));
        upsertPlayingHabit(userId, Map.of("play_preference", request.getPlayPreference()));
        writeEditLog(userId, "play_preference");
        return getProfile(userId);
    }

    public List<HabitCourtResponse> searchCourts(String country, String province, String city, String keyword) {
        String normalizedCountry = normalizeNullable(country);
        String normalizedProvince = normalizeNullable(province);
        String normalizedCity = normalizeNullable(city);
        String normalizedKeyword = normalizeNullable(keyword);
        return mobileProfileMapper.searchCourts(normalizedCountry, normalizedProvince, normalizedCity, normalizedKeyword)
                .stream()
                .map(this::toHabitCourtResponse)
                .toList();
    }

    public List<UserSearchResponse> searchUsers(String keyword) {
        return searchUsers(keyword, null, false, 100);
    }

    public List<UserSearchResponse> searchUsers(String keyword, String currentUserId, boolean followingOnly, Integer limit) {
        String normalizedKeyword = normalizeNullable(keyword);
        String normalizedCurrentUserId = normalizeNullable(currentUserId);
        if (!StringUtils.hasText(normalizedKeyword) && !followingOnly) {
            return List.of();
        }
        int safeLimit = Math.max(1, Math.min(limit == null ? 100 : limit, 200));
        int rowLimit = safeLimit * 3;

        Map<String, UserSearchResponse.UserSearchResponseBuilder> builders = new LinkedHashMap<>();
        Map<String, List<String>> courtNames = new LinkedHashMap<>();
        for (Map<String, Object> row : mobileProfileMapper.searchUsers(
                normalizedKeyword,
                normalizedCurrentUserId,
                followingOnly,
                rowLimit
        )) {
            String userId = (String) rowValue(row, "id");
            if (!StringUtils.hasText(userId)) {
                continue;
            }
            builders.computeIfAbsent(userId, ignored -> UserSearchResponse.builder()
                    .id(userId)
                    .displayName((String) rowValue(row, "display_name"))
                    .avatarUrl((String) rowValue(row, "avatar_url"))
                    .gender((String) rowValue(row, "gender"))
                    .ntrpRating(numberAsDouble(rowValue(row, "ntrp_rating"), null))
                    .region(regionText(row))
                    .followingCount(integerValue(rowValue(row, "following_count")))
                    .followerCount(integerValue(rowValue(row, "follower_count")))
                    .isFollowing(booleanValue(rowValue(row, "is_following")))
            );

            String courtName = (String) rowValue(row, "court_name");
            if (StringUtils.hasText(courtName)) {
                List<String> names = courtNames.computeIfAbsent(userId, ignored -> new ArrayList<>());
                if (names.size() < 3 && !names.contains(courtName)) {
                    names.add(courtName);
                }
            }
        }

        return builders.entrySet()
                .stream()
                .limit(safeLimit)
                .map(entry -> entry.getValue()
                        .habitCourts(courtNames.getOrDefault(entry.getKey(), List.of()))
                        .build())
                .toList();
    }

    public UserSearchResponse getFounderUser(String currentUserId) {
        String normalizedCurrentUserId = normalizeNullable(currentUserId);
        Map<String, Object> user = mobileProfileMapper.findUserByEmail(FOUNDER_EMAIL);
        if (user == null
                || rowValue(user, "deleted_at") != null
                || !Objects.equals(integerValue(rowValue(user, "status")), 0)) {
            throw new BusinessException(10004, "开发者账号不存在");
        }

        String userId = (String) rowValue(user, "id");
        boolean isFollowing = StringUtils.hasText(normalizedCurrentUserId)
                && !normalizedCurrentUserId.equals(userId)
                && mobileProfileMapper.countFollowRelationship(normalizedCurrentUserId, userId) > 0;
        List<String> courtNames = mobileProfileMapper.findHabitCourtsByUserId(userId)
                .stream()
                .map(row -> (String) rowValue(row, "name"))
                .filter(StringUtils::hasText)
                .distinct()
                .limit(3)
                .toList();

        return UserSearchResponse.builder()
                .id(userId)
                .displayName((String) rowValue(user, "display_name"))
                .avatarUrl((String) rowValue(user, "avatar_url"))
                .gender((String) rowValue(user, "gender"))
                .ntrpRating(numberAsDouble(rowValue(user, "ntrp_rating"), null))
                .region(regionText(user))
                .habitCourts(courtNames)
                .followingCount(mobileProfileMapper.countFollowing(userId))
                .followerCount(mobileProfileMapper.countFollowers(userId))
                .isFollowing(isFollowing)
                .build();
    }

    @Transactional
    public UserFollowResponse followUser(String userId, String targetUserId) {
        requireUser(userId);
        String normalizedTargetUserId = normalize(targetUserId);
        if (userId.equals(normalizedTargetUserId)) {
            throw new BusinessException(10001, "不能关注自己");
        }
        requireUser(normalizedTargetUserId);

        mobileProfileMapper.upsertFollowRelationship(UUID.randomUUID().toString(), userId, normalizedTargetUserId);
        int followingCount = refreshFollowingCountCache(userId);
        writeEditLog(userId, "follow_user");
        return UserFollowResponse.builder()
                .userId(userId)
                .targetUserId(normalizedTargetUserId)
                .followed(true)
                .followingCount(followingCount)
                .build();
    }

    public List<RacketCatalogResponse> listRacketCatalog() {
        return mobileProfileMapper.listRacketCatalog()
                .stream()
                .map(this::toRacketCatalogResponse)
                .toList();
    }

    public List<RacketPlayerUsageResponse> listRacketPlayerUsages(String catalogId) {
        Map<String, Object> catalog = mobileProfileMapper.findRacketCatalogById(catalogId);
        if (catalog == null) {
            throw new BusinessException(10004, "球拍不存在");
        }
        String brand = (String) rowValue(catalog, "brand");
        String model = (String) rowValue(catalog, "model");
        return mobileProfileMapper.listRacketPlayerUsages(brand, model)
                .stream()
                .map(this::toRacketPlayerUsageResponse)
                .toList();
    }

    public List<ActivityRecordResponse> listActivityRecords(String userId, String scope) {
        requireUser(userId);
        List<Map<String, Object>> rows = "partners".equalsIgnoreCase(scope)
                ? mobileProfileMapper.findVisibleActivityRecordsByFollowing(userId)
                : mobileProfileMapper.findActivityRecordsByUserId(userId);
        return rows
                .stream()
                .map(row -> toActivityRecordResponse(row, userId))
                .toList();
    }

    @Transactional
    public ActivityRecordResponse likeActivityRecord(String userId, String recordId) {
        requireUser(userId);
        Map<String, Object> record = mobileProfileMapper.findPlaySessionById(recordId, userId);
        if (record == null) {
            throw new BusinessException(10004, "活动记录不存在");
        }
        String ownerId = (String) rowValue(record, "owner_id");
        mobileProfileMapper.insertActivityRecordLikeDetail(UUID.randomUUID().toString(), recordId, userId);
        mobileProfileMapper.upsertActivityRecordLikeStats(recordId, ownerId);
        Map<String, Object> updated = mobileProfileMapper.findPlaySessionById(recordId, userId);
        return toActivityRecordResponse(updated == null ? record : updated, userId);
    }

    public List<MatchRequestResponse> listMatchRequests(String userId, String country, String province, String city) {
        requireUser(userId);
        String normalizedCountry = normalizeNullable(country);
        String normalizedProvince = normalizeNullable(province);
        String normalizedCity = normalizeNullable(city);
        return mobileProfileMapper.findMatchRequests(normalizedCountry, normalizedProvince, normalizedCity)
                .stream()
                .map(this::toMatchRequestResponse)
                .toList();
    }

    @Transactional
    public MatchRequestResponse createMatchRequest(String userId, MatchRequestCreateRequest request) {
        requireUser(userId);
        LocalDateTime startedAt = parseStartedAt(request.getStartedAt());
        int durationMinutes = request.getDurationMinutes() == null ? 120 : request.getDurationMinutes();
        if (durationMinutes <= 0 || durationMinutes > 1440) {
            throw new BusinessException(10001, "活动时长不正确");
        }

        String matchType = Optional.ofNullable(normalizeNullable(request.getMatchType())).orElse("双打");
        int neededPlayers = request.getNeededPlayers() == null ? 1 : request.getNeededPlayers();
        if (neededPlayers <= 0 || neededPlayers > 20) {
            throw new BusinessException(10001, "缺少人数不正确");
        }
        Double minLevel = request.getMinLevel();
        Double maxLevel = request.getMaxLevel();
        if (minLevel != null && maxLevel != null && minLevel > maxLevel) {
            throw new BusinessException(10001, "NTRP范围不正确");
        }

        String courtId = normalizeNullable(request.getCourtId());
        String courtName = normalizeNullable(request.getCourtName());
        CourtDO court = null;
        if (StringUtils.hasText(courtId)) {
            court = mobileProfileMapper.findActiveCourtById(courtId);
            if (court == null) {
                throw new BusinessException(10004, "球场不存在");
            }
            courtName = court.getName();
        } else if (StringUtils.hasText(courtName)) {
            court = mobileProfileMapper.findActiveCourtByName(courtName);
            if (court != null) {
                courtId = court.getId();
            }
        }

        String note = normalizeNullable(request.getNote());
        if (StringUtils.hasText(note)) {
            contentSafetyService.assertSafeText(note, "约球备注");
        }

        String eventId = UUID.randomUUID().toString();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", eventId);
        values.put("organizer_id", userId);
        values.put("title", matchType + "缺" + neededPlayers + "人");
        values.put("court_id", courtId);
        values.put("court_name", courtName);
        values.put("country", Optional.ofNullable(normalizeNullable(request.getCountry())).orElse(court == null ? null : court.getCountry()));
        values.put("province", Optional.ofNullable(normalizeNullable(request.getProvince())).orElse(court == null ? null : court.getProvince()));
        values.put("city", Optional.ofNullable(normalizeNullable(request.getCity())).orElse(court == null ? null : court.getCity()));
        values.put("district", normalizeNullable(request.getDistrict()));
        values.put("started_at", startedAt);
        values.put("ended_at", startedAt.plusMinutes(durationMinutes));
        values.put("match_type", matchType);
        values.put("needed_players", neededPlayers);
        values.put("min_level", minLevel);
        values.put("max_level", maxLevel);
        values.put("price_mode", Optional.ofNullable(normalizeNullable(request.getPriceMode())).orElse("AA"));
        values.put("price_per_person", request.getPricePerPerson());
        values.put("gender_requirement", normalizeNullable(request.getGenderRequirement()));
        values.put("note", note);
        values.put("max_participants", neededPlayers + 1);
        try {
            mobileProfileMapper.insertMatchRequest(values);
        } catch (DataAccessException e) {
            log.error("Create match request DB write failed. userId={}, values={}", userId, values, e);
            throw e;
        }
        writeEditLog(userId, "match_request");

        Map<String, Object> created = mobileProfileMapper.findMatchRequestById(eventId);
        return toMatchRequestResponse(created == null ? values : created);
    }

    @Transactional
    public ActivityRecordResponse createActivityRecord(String userId, ActivityRecordCreateRequest request) {
        Map<String, Object> user = requireUser(userId);
        String courtId = normalizeNullable(request.getCourtId());
        CourtDO court = null;
        if (StringUtils.hasText(courtId)) {
            court = mobileProfileMapper.findActiveCourtById(courtId);
            if (court == null) {
                throw new BusinessException(10004, "球场不存在");
            }
        }

        LocalDateTime startedAt = parseStartedAt(request.getStartedAt());
        int durationMinutes = request.getDurationMinutes() == null ? 120 : request.getDurationMinutes();
        if (durationMinutes <= 0 || durationMinutes > 1440) {
            throw new BusinessException(10001, "活动时长不正确");
        }

        String partnerName = normalizeNullable(request.getPartnerName());
        if (StringUtils.hasText(partnerName)) {
            contentSafetyService.assertSafeText(partnerName, "球友备注");
        }

        String sessionId = UUID.randomUUID().toString();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", sessionId);
        values.put("owner_id", userId);
        values.put("sport_type", "tennis");
        values.put("session_type", "rally");
        values.put("title", "打球记录");
        values.put("started_at", startedAt);
        values.put("ended_at", startedAt.plusMinutes(durationMinutes));
        values.put("duration_minutes", durationMinutes);
        values.put("court_id", court == null ? null : court.getId());
        values.put("court_name", court == null ? null : court.getName());
        values.put("notes", StringUtils.hasText(partnerName) ? "跟 " + partnerName : null);
        values.put("privacy_level", "matchedPlayers");
        try {
            mobileProfileMapper.insertPlaySession(values);
        } catch (DataAccessException e) {
            logDataAccessFailure("Create activity record DB write failed", e,
                    "userId", userId,
                    "startedAt", startedAt,
                    "durationMinutes", durationMinutes,
                    "courtId", courtId,
                    "values", values);
            throw e;
        }
        tryWriteEditLog(userId, "activity_record", "activity record create");

        Map<String, Object> created;
        try {
            created = mobileProfileMapper.findPlaySessionById(sessionId, userId);
        } catch (DataAccessException e) {
            logDataAccessFailure("Query created activity record DB failed", e,
                    "userId", userId,
                    "sessionId", sessionId);
            created = null;
        }
        if (created == null) {
            created = new LinkedHashMap<>(values);
            created.put("owner_name", rowValue(user, "display_name"));
            created.put("owner_avatar_url", rowValue(user, "avatar_url"));
            created.put("city", court == null ? null : court.getCity());
            created.put("address", court == null ? null : court.getAddress());
        }
        return toActivityRecordResponse(created, userId);
    }

    public List<FitnessWorkoutImportStatusResponse> findFitnessWorkoutImportStatuses(
            String userId,
            FitnessWorkoutImportStatusRequest request
    ) {
        requireUser(userId);
        List<String> uuids = Optional.ofNullable(request.getHealthkitUuids())
                .orElseGet(List::of)
                .stream()
                .map(this::normalizeNullable)
                .filter(StringUtils::hasText)
                .distinct()
                .limit(200)
                .toList();
        Set<String> imported;
        try {
            imported = new HashSet<>(mobileProfileMapper.findImportedPlaySessionHealthkitUuids(userId, uuids));
        } catch (DataAccessException e) {
            logDataAccessFailure("Query fitness workout import statuses DB failed", e,
                    "userId", userId,
                    "uuidCount", uuids.size(),
                    "sampleUuids", sampleValues(uuids, 5));
            throw e;
        }
        return uuids.stream()
                .map(uuid -> FitnessWorkoutImportStatusResponse.builder()
                        .healthkitUuid(uuid)
                        .imported(imported.contains(uuid))
                        .build())
                .toList();
    }

    @Transactional
    public ActivityRecordResponse importFitnessWorkoutAsActivityRecord(String userId, FitnessWorkoutSessionRequest request) {
        Map<String, Object> user = requireUser(userId);
        String healthkitUuid = normalize(request.getHealthkitUuid());
        Map<String, Object> existing;
        try {
            existing = mobileProfileMapper.findPlaySessionByHealthkitUuid(userId, healthkitUuid);
        } catch (DataAccessException e) {
            logDataAccessFailure("Query imported fitness workout DB failed, continue to insert", e,
                    "userId", userId,
                    "healthkitUuid", healthkitUuid);
            existing = null;
        }
        if (existing != null) {
            return toActivityRecordResponse(existing, userId);
        }

        try {
            Map<String, Object> workoutValues = fitnessWorkoutValues(request);
            mobileProfileMapper.upsertFitnessWorkoutSession(UUID.randomUUID().toString(), userId, workoutValues);
            log.info("Imported HealthKit workout raw record saved. userId={}, healthkitUuid={}, sportType={}, startedAt={}",
                    userId,
                    workoutValues.get("healthkit_uuid"),
                    workoutValues.get("sport_type"),
                    workoutValues.get("started_at"));
        } catch (DataAccessException e) {
            logDataAccessFailure("Import fitness workout raw record DB upsert failed, continue to create activity record", e,
                    "userId", userId,
                    "healthkitUuid", healthkitUuid,
                    "sportType", request.getSportType(),
                    "startedAt", request.getStartedAt());
        }

        LocalDateTime startedAt = parseFitnessDateTime(request.getStartedAt(), "训练开始时间");
        LocalDateTime endedAt = parseNullableFitnessDateTime(request.getEndedAt(), "训练结束时间");
        int durationMinutes = request.getDurationSeconds() == null
                ? 60
                : Math.max(1, (int) Math.round(request.getDurationSeconds() / 60.0));
        if (endedAt == null) {
            endedAt = startedAt.plusMinutes(durationMinutes);
        }

        String sportType = Optional.ofNullable(normalizeNullable(request.getSportType())).orElse("other");
        String sportLabel = fitnessSportLabel(sportType);
        String title = sportLabel + "运动记录";

        List<String> noteParts = new ArrayList<>();
        noteParts.add("运动类型 " + sportLabel);
        if (request.getTotalEnergyKcal() != null || request.getActiveEnergyKcal() != null) {
            Double kcal = request.getTotalEnergyKcal() != null ? request.getTotalEnergyKcal() : request.getActiveEnergyKcal();
            noteParts.add("热量 " + Math.round(kcal) + " 千卡");
        }
        if (request.getDistanceMeters() != null) {
            noteParts.add("距离 " + Math.round(request.getDistanceMeters()) + " 米");
        }

        String sessionId = UUID.randomUUID().toString();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", sessionId);
        values.put("owner_id", userId);
        values.put("sport_type", "tennis");
        values.put("session_type", "training");
        values.put("title", title);
        values.put("started_at", startedAt);
        values.put("ended_at", endedAt);
        values.put("duration_minutes", durationMinutes);
        values.put("court_id", null);
        values.put("court_name", null);
        values.put("notes", noteParts.isEmpty() ? "来自 HealthKit" : String.join(" · ", noteParts));
        values.put("privacy_level", "matchedPlayers");
        values.put("healthkit_uuid", healthkitUuid);
        try {
            mobileProfileMapper.insertPlaySession(values);
        } catch (DataAccessException e) {
            logDataAccessFailure("Import fitness workout DB write failed", e,
                    "userId", userId,
                    "healthkitUuid", healthkitUuid,
                    "startedAt", startedAt,
                    "durationMinutes", durationMinutes,
                    "values", values);
            throw e;
        }
        tryWriteEditLog(userId, "fitness_activity_record", "fitness workout import");

        Map<String, Object> created;
        try {
            created = mobileProfileMapper.findPlaySessionById(sessionId, userId);
        } catch (DataAccessException e) {
            logDataAccessFailure("Query imported fitness workout activity record DB failed", e,
                    "userId", userId,
                    "sessionId", sessionId,
                    "healthkitUuid", healthkitUuid);
            created = null;
        }
        if (created == null) {
            created = new LinkedHashMap<>(values);
            created.put("owner_name", rowValue(user, "display_name"));
            created.put("owner_avatar_url", rowValue(user, "avatar_url"));
        }
        return toActivityRecordResponse(created, userId);
    }

    private Map<String, Object> fitnessWorkoutValues(FitnessWorkoutSessionRequest workout) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("healthkit_uuid", normalize(workout.getHealthkitUuid()));
        values.put("sport_type", normalize(workout.getSportType()));
        values.put("started_at", parseFitnessDateTime(workout.getStartedAt(), "训练开始时间"));
        values.put("ended_at", parseNullableFitnessDateTime(workout.getEndedAt(), "训练结束时间"));
        values.put("duration_seconds", workout.getDurationSeconds());
        values.put("active_energy_kcal", workout.getActiveEnergyKcal());
        values.put("basal_energy_kcal", workout.getBasalEnergyKcal());
        values.put("total_energy_kcal", workout.getTotalEnergyKcal());
        values.put("distance_meters", workout.getDistanceMeters());
        values.put("avg_heart_rate", workout.getAvgHeartRate());
        values.put("max_heart_rate", workout.getMaxHeartRate());
        values.put("heart_rate_zones", null);
        values.put("pace_seconds_per_km", workout.getPaceSecondsPerKm());
        values.put("speed_mps", workout.getSpeedMps());
        values.put("elevation_gain_meters", workout.getElevationGainMeters());
        values.put("cadence", workout.getCadence());
        values.put("stroke_count", workout.getStrokeCount());
        values.put("source_name", normalizeNullable(workout.getSourceName()));
        values.put("source_bundle_id", normalizeNullable(workout.getSourceBundleId()));
        values.put("device_model", normalizeNullable(workout.getDeviceModel()));
        values.put("notes", normalizeNullable(workout.getNotes()));
        values.put("raw_payload", null);
        return values;
    }

    private String fitnessSportLabel(String sportType) {
        if (!StringUtils.hasText(sportType)) {
            return "健身";
        }
        return switch (sportType) {
            case "tennis" -> "网球";
            case "running" -> "跑步";
            case "cycling" -> "骑行";
            case "swimming" -> "游泳";
            case "strength_training" -> "力量训练";
            case "yoga" -> "瑜伽";
            case "hiit" -> "HIIT";
            case "hiking" -> "徒步";
            case "walking" -> "步行";
            case "elliptical" -> "椭圆机";
            case "rowing" -> "划船";
            case "core_training" -> "核心训练";
            case "pilates" -> "普拉提";
            case "dance" -> "舞蹈";
            default -> "其他训练";
        };
    }

    private String fitnessPlaySessionSportType(String sportType) {
        if (!StringUtils.hasText(sportType)) {
            return "other";
        }
        return switch (sportType) {
            case "tennis", "running", "cycling", "swimming", "yoga", "hiit", "hiking",
                 "walking", "elliptical", "rowing", "pilates", "dance", "other" -> sportType;
            case "strength_training", "traditionalStrengthTraining", "functionalStrengthTraining" -> "strength_training";
            case "core_training" -> "core_training";
            case "highIntensityIntervalTraining" -> "hiit";
            default -> sportType.length() <= 20 ? sportType : "other";
        };
    }

    private List<String> sampleValues(List<String> values, int limit) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return values.stream().limit(limit).toList();
    }

    @Transactional
    public RacketCatalogResponse createRacketCatalog(String userId, RacketCatalogCreateRequest request) {
        requireAdmin(userId);
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", UUID.randomUUID().toString());
        values.put("brand", normalize(request.getBrand()));
        values.put("model", normalize(request.getModel()));
        values.put("model_zh", normalizeNullable(request.getModelZh()));
        values.put("unstrung_weight_gram", positiveIntegerOrNull(request.getUnstrungWeightGram(), "空拍质量"));
        values.put("string_pattern", normalizeNullable(request.getStringPattern()));
        values.put("balance_point_mm", positiveIntegerOrNull(request.getBalancePointMm(), "平衡点"));
        values.put("length_inch", positiveDoubleOrNull(request.getLengthInch(), "长度"));
        values.put("grip_size", normalizeNullable(request.getGripSize()));
        values.put("release_year", positiveIntegerOrNull(request.getReleaseYear(), "年份"));
        values.put("image_url", normalizeNullable(request.getImageUrl()));
        try {
            mobileProfileMapper.insertRacketCatalog(values);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(10001, "该球拍基础数据已存在");
        }
        return toRacketCatalogResponse(values);
    }

    @Transactional
    public UserRacketAddResponse addUserRacket(String userId, UserRacketAddRequest request) {
        requireUser(userId);
        Map<String, Object> catalog = mobileProfileMapper.findRacketCatalogById(request.getCatalogId());
        if (catalog == null) {
            throw new BusinessException(10004, "球拍不存在");
        }
        String bagId = ensureEquipmentBag(userId);
        String racketId = UUID.randomUUID().toString();
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", racketId);
        values.put("bag_id", bagId);
        values.put("brand", rowValue(catalog, "brand"));
        values.put("model", rowValue(catalog, "model"));
        values.put("grip_size", rowValue(catalog, "grip_size"));
        values.put("weight_gram", rowValue(catalog, "unstrung_weight_gram"));
        values.put("image_url", rowValue(catalog, "image_url"));
        values.put("is_primary", mobileProfileMapper.countRacketsByUserId(userId) == 0 ? 1 : 0);
        values.put("display_order", 0);
        mobileProfileMapper.insertUserRacket(values);
        writeEditLog(userId, "racket");
        return UserRacketAddResponse.builder()
                .racketId(racketId)
                .racketCount(mobileProfileMapper.countRacketsByUserId(userId))
                .message("球拍已添加到我的装备")
                .build();
    }

    public HabitCourtResponse getCourt(String userId, String courtId) {
        CourtDO court = mobileProfileMapper.findActiveCourtById(courtId);
        if (court == null) {
            throw new BusinessException(10004, "网球场不存在");
        }
        return toHabitCourtResponse(court, userId);
    }

    @Transactional
    public CourtLikeResponse likeCourt(String userId, String courtId) {
        requireUser(userId);
        CourtDO court = mobileProfileMapper.findActiveCourtById(courtId);
        if (court == null) {
            throw new BusinessException(10004, "网球场不存在");
        }
        mobileProfileMapper.insertCourtLike(UUID.randomUUID().toString(), courtId, userId);
        return courtLikeResponse(courtId, userId);
    }

    @Transactional
    public List<HabitCourtResponse> addHabitCourt(String userId, HabitCourtAddRequest request) {
        requireUser(userId);
        CourtDO court = mobileProfileMapper.findActiveCourtById(request.getCourtId());
        if (court == null) {
            throw new BusinessException(10004, "网球场不存在");
        }
        String habitId = ensurePlayingHabit(userId);
        boolean isPrimary = Boolean.TRUE.equals(request.getIsPrimary());
        if (isPrimary) {
            mobileProfileMapper.deactivatePrimaryHabitCourts(habitId);
        }
        mobileProfileMapper.upsertHabitCourt(UUID.randomUUID().toString(), habitId, request.getCourtId(), isPrimary);
        writeEditLog(userId, "habit_court");
        return findHabitCourts(userId);
    }

    @Transactional
    public List<HabitCourtResponse> removeHabitCourt(String userId, String courtId) {
        requireUser(userId);
        CourtDO court = mobileProfileMapper.findActiveCourtById(courtId);
        if (court == null) {
            throw new BusinessException(10004, "网球场不存在");
        }
        Map<String, Object> habit = mobileProfileMapper.findActivePlayingHabitByUserId(userId);
        if (habit == null) {
            return List.of();
        }
        String habitId = (String) rowValue(habit, "id");
        mobileProfileMapper.deactivateHabitCourt(habitId, courtId);
        writeEditLog(userId, "habit_court_remove");
        return findHabitCourts(userId);
    }

    @Transactional
    public CourtSubmissionResponse submitCourt(String userId, CourtSubmissionRequest request) {
        requireUser(userId);
        String courtId = UUID.randomUUID().toString();
        String courtName = normalize(request.getName());
        if (mobileProfileMapper.findActiveCourtByName(courtName) != null) {
            throw new BusinessException(10001, "网球场名称已存在");
        }

        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", courtId);
        values.put("country", normalize(request.getCountry()));
        values.put("province", normalize(request.getProvince()));
        values.put("city", normalize(request.getCity()));
        values.put("name", courtName);
        values.put("address", normalizeNullable(request.getAddress()));
        values.put("contact_phone", normalizeNullable(request.getContactPhone()));
        values.put("wechat_mini_program_name", normalizeNullable(request.getWechatMiniProgramName()));
        values.put("photo_urls", jsonPhotoUrls(request.getPhotoUrls()));
        values.put("description", normalizeNullable(request.getDescription()));
        values.put("latitude", request.getLatitude());
        values.put("longitude", request.getLongitude());
        values.put("map_source", normalizeNullable(request.getMapSource()));
        values.put("surface_type", normalizeNullable(request.getSurfaceType()));
        values.put("has_indoor", request.getHasIndoor());
        values.put("has_outdoor", request.getHasOutdoor());
        values.put("indoor_outdoor", indoorOutdoorValue(request.getHasIndoor(), request.getHasOutdoor()));
        values.put("total_court_count", courtCountValue(request.getTotalCourtCount()));
        values.put("opening_time", timeValue(request.getOpeningTime(), "营业开始时间"));
        values.put("closing_time", timeValue(request.getClosingTime(), "营业结束时间"));
        values.put("created_by", userId);
        values.put("venue_status", "pending_review");
        values.put("approval_status", "pending");
        values.put("operator_managed", 0);

        contentSafetyService.assertSafeText((String) values.get("name"), "球场名字");
        if (StringUtils.hasText((String) values.get("address"))) {
            contentSafetyService.assertSafeText((String) values.get("address"), "球场地址");
        }
        contentSafetyService.assertSafeText((String) values.get("description"), "球场描述");
        mobileProfileMapper.insertCourt(values);
        writeEditLog(userId, "court_submission");

        return CourtSubmissionResponse.builder()
                .courtId(courtId)
                .approvalStatus("pending")
                .venueStatus("pending_review")
                .message("已提交审核，审核通过后会进入球场库")
                .build();
    }

    @Transactional
    public CourtSubmissionResponse submitCourtChangeRequest(String userId, String courtId, CourtSubmissionRequest request) {
        requireUser(userId);
        CourtDO court = mobileProfileMapper.findActiveCourtById(courtId);
        if (court == null) {
            throw new BusinessException(10004, "网球场不存在");
        }
        if (!"approved".equals(court.getApprovalStatus()) || !"active".equals(court.getVenueStatus())) {
            throw new BusinessException(10001, "网球场审核通过后才允许申请修改");
        }
        if (mobileProfileMapper.countPendingCourtChangeRequest(courtId) > 0) {
            throw new BusinessException(10001, "该网球场已有修改申请正在审核中");
        }

        Map<String, Object> values = courtValues(request);
        values.put("id", UUID.randomUUID().toString());
        values.put("court_id", courtId);
        values.put("submitted_by", userId);

        CourtDO sameNameCourt = mobileProfileMapper.findActiveCourtByName((String) values.get("name"));
        if (sameNameCourt != null && !courtId.equals(sameNameCourt.getId())) {
            throw new BusinessException(10001, "网球场名称已存在");
        }

        assertCourtContentSafe(values);
        try {
            mobileProfileMapper.insertCourtChangeRequest(values);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(10001, "该网球场已有修改申请正在审核中");
        }
        int updated = mobileProfileMapper.markCourtPendingReviewForChange(courtId);
        if (updated != 1) {
            log.error("Failed to mark court pending review after change request. courtId={}, userId={}, updated={}",
                    courtId,
                    userId,
                    updated);
            throw new BusinessException(10001, "网球场状态已变化，请刷新后重试");
        }
        writeEditLog(userId, "court_change_request");

        return CourtSubmissionResponse.builder()
                .courtId(courtId)
                .approvalStatus("pending")
                .venueStatus("pending_review")
                .message("修改申请已提交，管理员审核通过后会更新球场信息")
                .build();
    }

    private Map<String, Object> requireUser(String userId) {
        Map<String, Object> user = mobileProfileMapper.findActiveUserById(userId);
        if (user == null) {
            Map<String, Object> existingUser = mobileProfileMapper.findUserById(userId);
            if (existingUser == null) {
                log.error("User not found: userId={}", userId);
            } else {
                log.error(
                        "User found but filtered as inactive: userId={}, status={}, deletedAt={}",
                        userId,
                        rowValue(existingUser, "status"),
                        rowValue(existingUser, "deleted_at")
                );
            }
            throw new BusinessException(10004, "用户不存在");
        }
        return user;
    }

    private void requireAdmin(String userId) {
        Map<String, Object> user = requireUser(userId);
        String email = (String) rowValue(user, "email");
        if (!StringUtils.hasText(email) || !MOBILE_ADMIN_EMAIL.equalsIgnoreCase(email)) {
            throw new BusinessException(10003, "只有管理员可以维护球拍基础数据");
        }
    }

    private void updateUser(String userId, Map<String, Object> values) {
        requireUser(userId);
        mobileProfileMapper.updateUser(userId, values);
    }

    private void updateUserNullable(String userId, String fieldName, Object value) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put(fieldName, value);
        updateUser(userId, values);
    }

    private Optional<Map<String, Object>> findTennisProfile(String userId) {
        return Optional.ofNullable(mobileProfileMapper.findActiveTennisProfileByUserId(userId));
    }

    private String ensureTennisProfile(String userId) {
        return findTennisProfile(userId).map(row -> (String) rowValue(row, "id")).orElseGet(() -> {
            String id = UUID.randomUUID().toString();
            mobileProfileMapper.insertTennisProfile(id, userId);
            return id;
        });
    }

    private void upsertTennisProfile(String userId, Map<String, Object> values) {
        String profileId = ensureTennisProfile(userId);
        mobileProfileMapper.updateTennisProfile(profileId, values);
    }

    private Optional<Map<String, Object>> findSkillProfile(String userId) {
        return Optional.ofNullable(mobileProfileMapper.findActiveSkillProfileByUserId(userId));
    }

    private void upsertSkillProfile(String userId, Map<String, Object> values) {
        Optional<Map<String, Object>> existing = findSkillProfile(userId);
        if (existing.isPresent()) {
            mobileProfileMapper.updateSkillProfile((String) rowValue(existing.get(), "id"), values);
            return;
        }
        Map<String, Object> insertValues = new LinkedHashMap<>(values);
        insertValues.putIfAbsent("ntrp_rating", 3.5);
        insertValues.putIfAbsent("player_identity", "amateur");
        insertValues.put("id", UUID.randomUUID().toString());
        insertValues.put("user_id", userId);
        insertValues.put("status", 0);
        insertValues.put("confidence_score", 0);
        mobileProfileMapper.insertSkillProfile(insertValues);
    }

    private void upsertPlayingHabit(String userId, Map<String, Object> values) {
        Map<String, Object> playingHabit = mobileProfileMapper.findActivePlayingHabitByUserId(userId);
        if (playingHabit != null) {
            mobileProfileMapper.updatePlayingHabit((String) rowValue(playingHabit, "id"), values);
            return;
        }
        Map<String, Object> insertValues = new LinkedHashMap<>(values);
        insertValues.put("id", UUID.randomUUID().toString());
        insertValues.put("user_id", userId);
        insertValues.put("status", 0);
        mobileProfileMapper.insertPlayingHabit(insertValues);
    }

    private String ensurePlayingHabit(String userId) {
        Map<String, Object> playingHabit = mobileProfileMapper.findActivePlayingHabitByUserId(userId);
        if (playingHabit != null) {
            return (String) rowValue(playingHabit, "id");
        }
        String id = UUID.randomUUID().toString();
        Map<String, Object> insertValues = new LinkedHashMap<>();
        insertValues.put("id", id);
        insertValues.put("user_id", userId);
        insertValues.put("status", 0);
        mobileProfileMapper.insertPlayingHabit(insertValues);
        return id;
    }

    private String ensureEquipmentBag(String userId) {
        Map<String, Object> bag = mobileProfileMapper.findEquipmentBagByUserId(userId);
        if (bag != null) {
            return (String) rowValue(bag, "id");
        }
        String id = UUID.randomUUID().toString();
        mobileProfileMapper.insertEquipmentBag(id, userId);
        return id;
    }

    private RacketCatalogResponse toRacketCatalogResponse(Map<String, Object> row) {
        return RacketCatalogResponse.builder()
                .id((String) rowValue(row, "id"))
                .brand((String) rowValue(row, "brand"))
                .model((String) rowValue(row, "model"))
                .modelZh((String) rowValue(row, "model_zh"))
                .unstrungWeightGram(integerValue(rowValue(row, "unstrung_weight_gram")))
                .stringPattern((String) rowValue(row, "string_pattern"))
                .balancePointMm(integerValue(rowValue(row, "balance_point_mm")))
                .lengthInch(numberAsDouble(rowValue(row, "length_inch"), null))
                .gripSize((String) rowValue(row, "grip_size"))
                .releaseYear(integerValue(rowValue(row, "release_year")))
                .imageUrl((String) rowValue(row, "image_url"))
                .build();
    }

    private RacketPlayerUsageResponse toRacketPlayerUsageResponse(Map<String, Object> row) {
        return RacketPlayerUsageResponse.builder()
                .id((String) rowValue(row, "id"))
                .playerName((String) rowValue(row, "player_name"))
                .brand((String) rowValue(row, "brand"))
                .model((String) rowValue(row, "model"))
                .usageYear(integerValue(rowValue(row, "usage_year")))
                .notes((String) rowValue(row, "notes"))
                .build();
    }

    private List<HabitCourtResponse> findHabitCourts(String userId) {
        try {
            return mobileProfileMapper.findHabitCourtsByUserId(userId)
                .stream()
                .map(this::toHabitCourtResponse)
                .toList();
        } catch (RuntimeException e) {
            log.warn("Failed to load habit courts for userId={}, returning empty list", userId, e);
            return List.of();
        }
    }

    private List<RecentPlaySessionResponse> findRecentPlaySessions(String userId) {
        try {
            return mobileProfileMapper.findRecentPlaySessionsByUserId(userId)
                    .stream()
                    .map(this::toRecentPlaySessionResponse)
                    .toList();
        } catch (RuntimeException e) {
            log.warn("Failed to load recent play sessions for userId={}, returning empty list", userId, e);
            return List.of();
        }
    }

    private RecentPlaySessionResponse toRecentPlaySessionResponse(Map<String, Object> row) {
        LocalDateTime startedAt = localDateTimeValue(rowValue(row, "started_at"));
        String title = normalizeNullable((String) rowValue(row, "title"));
        String sessionType = (String) rowValue(row, "session_type");
        if (!StringUtils.hasText(title)) {
            title = sessionTypeLabel(sessionType) + "记录";
        }

        List<String> detailParts = new ArrayList<>();
        String courtName = normalizeNullable((String) rowValue(row, "court_name"));
        if (courtName != null) {
            detailParts.add(courtName);
        }
        Integer duration = integerValue(rowValue(row, "duration_minutes"));
        if (duration != null && duration > 0) {
            detailParts.add(durationText(duration));
        }
        String notes = normalizeNullable((String) rowValue(row, "notes"));
        if (notes != null) {
            detailParts.add(notes);
        }

        return RecentPlaySessionResponse.builder()
                .id((String) rowValue(row, "id"))
                .dateText(startedAt == null ? "" : startedAt.format(PROFILE_ACTIVITY_DATE_FORMATTER))
                .title(title)
                .detail(detailParts.isEmpty() ? "运动记录已保存" : String.join(" · ", detailParts))
                .mood(sessionTypeLabel(sessionType))
                .build();
    }

    private ActivityRecordResponse toActivityRecordResponse(Map<String, Object> row, String viewerUserId) {
        LocalDateTime startedAt = localDateTimeValue(rowValue(row, "started_at"));
        String courtName = normalizeNullable((String) rowValue(row, "court_name"));
        String notes = normalizeNullable((String) rowValue(row, "notes"));
        Integer duration = integerValue(rowValue(row, "duration_minutes"));
        String sessionType = (String) rowValue(row, "session_type");
        String title = normalizeNullable((String) rowValue(row, "title"));
        if (!StringUtils.hasText(title)) {
            title = sessionTypeLabel(sessionType) + "记录";
        }
        String partnerName = null;
        if (StringUtils.hasText(notes) && notes.startsWith("跟 ")) {
            partnerName = normalizeNullable(notes.substring(2));
        }

        String location = Arrays.asList(
                        normalizeNullable((String) rowValue(row, "city")),
                        normalizeNullable((String) rowValue(row, "district")),
                        normalizeNullable((String) rowValue(row, "address"))
                )
                .stream()
                .filter(Objects::nonNull)
                .distinct()
                .reduce((left, right) -> left + " · " + right)
                .orElse(courtName == null ? "球场待完善" : courtName);

        List<ActivityRecordResponse.FeedMetricResponse> metrics = new ArrayList<>();
        metrics.add(ActivityRecordResponse.FeedMetricResponse.builder()
                .label("时间")
                .value(duration == null || duration <= 0 ? "待完善" : compactDurationText(duration))
                .build());
        metrics.add(ActivityRecordResponse.FeedMetricResponse.builder()
                .label("球场")
                .value(courtName == null ? "待完善" : courtName)
                .build());
        metrics.add(ActivityRecordResponse.FeedMetricResponse.builder()
                .label("类型")
                .value(sessionTypeLabel(sessionType))
                .build());

        return ActivityRecordResponse.builder()
                .id((String) rowValue(row, "id"))
                .author(Optional.ofNullable(normalizeNullable((String) rowValue(row, "owner_name"))).orElse("我"))
                .avatarUrl((String) rowValue(row, "owner_avatar_url"))
                .meta(startedAt == null ? "iRallyIn" : startedAt.format(DateTimeFormatter.ofPattern("M月d日 HH:mm")) + " · iRallyIn")
                .location(location)
                .title(title)
                .body(notes == null ? (courtName == null ? "运动记录已保存" : courtName) : notes)
                .startedAt(startedAt == null ? null : startedAt.toString())
                .durationMinutes(duration)
                .partnerName(partnerName)
                .metrics(metrics)
                .badges(0)
                .likeCount(Optional.ofNullable(integerValue(rowValue(row, "like_count"))).orElse(0))
                .likedByMe(booleanValue(rowValue(row, "liked_by_me")))
                .recentLikers(toActivityRecordLikers((String) rowValue(row, "id")))
                .build();
    }

    private List<ActivityRecordResponse.ActivityRecordLikerResponse> toActivityRecordLikers(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            return List.of();
        }
        try {
            return mobileProfileMapper.findRecentActivityRecordLikers(sessionId)
                    .stream()
                    .map(row -> ActivityRecordResponse.ActivityRecordLikerResponse.builder()
                            .userId((String) rowValue(row, "user_id"))
                            .displayName(Optional.ofNullable(normalizeNullable((String) rowValue(row, "display_name"))).orElse("球友"))
                            .avatarUrl((String) rowValue(row, "avatar_url"))
                            .likeCount(Optional.ofNullable(integerValue(rowValue(row, "like_count"))).orElse(0))
                            .build())
                    .toList();
        } catch (DataAccessException e) {
            logDataAccessFailure("Query activity record recent likers DB failed, return empty likers", e,
                    "sessionId", sessionId);
            return List.of();
        }
    }

    private MatchRequestResponse toMatchRequestResponse(Map<String, Object> row) {
        LocalDateTime startedAt = localDateTimeValue(rowValue(row, "started_at"));
        String organizerName = normalizeNullable((String) rowValue(row, "organizer_name"));
        String courtName = normalizeNullable((String) rowValue(row, "court_name"));
        String city = normalizeNullable((String) rowValue(row, "city"));
        String district = normalizeNullable((String) rowValue(row, "district"));
        String priceMode = Optional.ofNullable(normalizeNullable((String) rowValue(row, "price_mode"))).orElse("AA");
        Integer pricePerPerson = integerValue(rowValue(row, "price_per_person"));
        Integer neededPlayers = integerValue(rowValue(row, "needed_players"));
        Double minLevel = numberAsDouble(rowValue(row, "min_level"), null);
        Double maxLevel = numberAsDouble(rowValue(row, "max_level"), null);

        return MatchRequestResponse.builder()
                .id((String) rowValue(row, "id"))
                .organizerId((String) rowValue(row, "organizer_id"))
                .organizerName(organizerName == null ? "球友" : organizerName)
                .avatarUrl((String) rowValue(row, "avatar_url"))
                .title((String) rowValue(row, "title"))
                .courtName(courtName == null ? "球场待定" : courtName)
                .country((String) rowValue(row, "country"))
                .province((String) rowValue(row, "province"))
                .city(city)
                .district(district)
                .areaText(matchAreaText(city, district))
                .startedAt(startedAt == null ? null : startedAt.toString())
                .timeText(matchTimeText(startedAt))
                .matchType((String) rowValue(row, "match_type"))
                .neededPlayers(neededPlayers)
                .minLevel(minLevel)
                .maxLevel(maxLevel)
                .levelText(matchLevelText(minLevel, maxLevel))
                .priceMode(priceMode)
                .pricePerPerson(pricePerPerson)
                .priceText(matchPriceText(priceMode, pricePerPerson))
                .note(matchNoteText((String) rowValue(row, "note"), neededPlayers))
                .distanceText("附近")
                .build();
    }

    private String matchAreaText(String city, String district) {
        return Arrays.asList(city, district)
                .stream()
                .filter(Objects::nonNull)
                .distinct()
                .reduce((left, right) -> left + " · " + right)
                .orElse("同城");
    }

    private String matchTimeText(LocalDateTime startedAt) {
        if (startedAt == null) {
            return "时间待定";
        }
        java.time.LocalDate date = startedAt.toLocalDate();
        java.time.LocalDate today = java.time.LocalDate.now();
        String dayText;
        if (date.equals(today)) {
            dayText = "今天";
        } else if (date.equals(today.plusDays(1))) {
            dayText = "明天";
        } else {
            dayText = startedAt.format(DateTimeFormatter.ofPattern("M月d日"));
        }
        return dayText + " " + startedAt.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String matchLevelText(Double minLevel, Double maxLevel) {
        if (minLevel == null && maxLevel == null) {
            return "NTRP不限";
        }
        if (minLevel != null && maxLevel != null && Double.compare(minLevel, maxLevel) == 0) {
            return String.format(Locale.US, "NTRP %.1f", minLevel);
        }
        if (minLevel != null && maxLevel != null) {
            return String.format(Locale.US, "NTRP %.1f-%.1f", minLevel, maxLevel);
        }
        return minLevel != null
                ? String.format(Locale.US, "NTRP %.1f+", minLevel)
                : String.format(Locale.US, "NTRP %.1f以下", maxLevel);
    }

    private String matchPriceText(String priceMode, Integer pricePerPerson) {
        if ("免费".equals(priceMode)) {
            return "免费";
        }
        if ("需要陪练".equals(priceMode)) {
            return pricePerPerson == null || pricePerPerson <= 0 ? "陪练" : "陪练 ¥" + pricePerPerson;
        }
        if ("可以陪练".equals(priceMode)) {
            return pricePerPerson == null || pricePerPerson <= 0 ? "可陪练" : "可陪练 ¥" + pricePerPerson;
        }
        return pricePerPerson == null || pricePerPerson <= 0 ? "AA" : "AA 约 ¥" + pricePerPerson;
    }

    private String matchNoteText(String note, Integer neededPlayers) {
        String normalized = normalizeNullable(note);
        if (normalized != null) {
            return normalized;
        }
        return "同城约球，缺" + (neededPlayers == null ? 1 : neededPlayers) + "人，一起打球。";
    }

    private String sessionTypeLabel(String sessionType) {
        if ("match".equals(sessionType)) {
            return "比赛";
        }
        if ("practiceMatch".equals(sessionType)) {
            return "练习赛";
        }
        if ("training".equals(sessionType)) {
            return "训练";
        }
        if ("rally".equals(sessionType)) {
            return "拉球";
        }
        return "打球";
    }

    private String durationText(Integer minutes) {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        if (hours > 0 && remainingMinutes > 0) {
            return hours + " 小时 " + remainingMinutes + " 分钟";
        }
        if (hours > 0) {
            return hours + " 小时";
        }
        return minutes + " 分钟";
    }

    private String compactDurationText(Integer minutes) {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        if (hours > 0 && remainingMinutes > 0) {
            return hours + "h " + remainingMinutes + "m";
        }
        if (hours > 0) {
            return hours + "h";
        }
        return minutes + "m";
    }

    private HabitCourtResponse toHabitCourtResponse(Map<String, Object> row) {
        return HabitCourtResponse.builder()
                .id((String) rowValue(row, "id"))
                .name((String) rowValue(row, "name"))
                .address((String) rowValue(row, "address"))
                .country((String) rowValue(row, "country"))
                .province((String) rowValue(row, "province"))
                .city((String) rowValue(row, "city"))
                .contactPhone((String) rowValue(row, "contact_phone"))
                .description((String) rowValue(row, "description"))
                .venueStatus((String) rowValue(row, "venue_status"))
                .approvalStatus((String) rowValue(row, "approval_status"))
                .createdBy((String) rowValue(row, "created_by"))
                .creatorName((String) rowValue(row, "creator_name"))
                .isPrimary(booleanValue(rowValue(row, "is_primary")))
                .latitude(numberAsDouble(rowValue(row, "latitude"), null))
                .longitude(numberAsDouble(rowValue(row, "longitude"), null))
                .mapSource((String) rowValue(row, "map_source"))
                .surfaceType((String) rowValue(row, "surface_type"))
                .indoorOutdoor((String) rowValue(row, "indoor_outdoor"))
                .hasIndoor(booleanValue(rowValue(row, "has_indoor")))
                .hasOutdoor(booleanValue(rowValue(row, "has_outdoor")))
                .totalCourtCount(integerValue(rowValue(row, "total_court_count")))
                .indoorCourtCount(integerValue(rowValue(row, "indoor_court_count")))
                .outdoorCourtCount(integerValue(rowValue(row, "outdoor_court_count")))
                .openingTime(localTimeValue(rowValue(row, "opening_time")))
                .closingTime(localTimeValue(rowValue(row, "closing_time")))
                .likeCount(0)
                .likedByMe(false)
                .build();
    }

    private HabitCourtResponse toHabitCourtResponse(CourtDO court) {
        return toHabitCourtResponse(court, null);
    }

    private HabitCourtResponse toHabitCourtResponse(CourtDO court, String userId) {
        return HabitCourtResponse.builder()
                .id(court.getId())
                .name(court.getName())
                .address(court.getAddress())
                .country(court.getCountry())
                .province(court.getProvince())
                .city(court.getCity())
                .contactPhone(court.getContactPhone())
                .description(court.getDescription())
                .venueStatus(court.getVenueStatus())
                .approvalStatus(court.getApprovalStatus())
                .createdBy(court.getCreatedBy())
                .creatorName(court.getCreatorName())
                .isPrimary(false)
                .latitude(court.getLatitude())
                .longitude(court.getLongitude())
                .mapSource(court.getMapSource())
                .surfaceType(court.getSurfaceType())
                .indoorOutdoor(court.getIndoorOutdoor())
                .hasIndoor(court.getHasIndoor())
                .hasOutdoor(court.getHasOutdoor())
                .totalCourtCount(court.getTotalCourtCount())
                .indoorCourtCount(court.getIndoorCourtCount())
                .outdoorCourtCount(court.getOutdoorCourtCount())
                .openingTime(court.getOpeningTime())
                .closingTime(court.getClosingTime())
                .likeCount(mobileProfileMapper.countCourtLikes(court.getId()))
                .likedByMe(StringUtils.hasText(userId) && mobileProfileMapper.countCourtLikeByUser(court.getId(), userId) > 0)
                .build();
    }

    private CourtLikeResponse courtLikeResponse(String courtId, String userId) {
        return CourtLikeResponse.builder()
                .courtId(courtId)
                .likeCount(mobileProfileMapper.countCourtLikes(courtId))
                .likedByMe(StringUtils.hasText(userId) && mobileProfileMapper.countCourtLikeByUser(courtId, userId) > 0)
                .build();
    }

    private String regionText(Map<String, Object> row) {
        return List.of("province", "city", "district")
                .stream()
                .map(key -> (String) rowValue(row, key))
                .filter(StringUtils::hasText)
                .distinct()
                .reduce((left, right) -> left + " · " + right)
                .orElse(null);
    }

    private Map<String, Object> courtValues(CourtSubmissionRequest request) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("country", normalize(request.getCountry()));
        values.put("province", normalize(request.getProvince()));
        values.put("city", normalize(request.getCity()));
        values.put("name", normalize(request.getName()));
        values.put("address", normalizeNullable(request.getAddress()));
        values.put("contact_phone", normalizeNullable(request.getContactPhone()));
        values.put("wechat_mini_program_name", normalizeNullable(request.getWechatMiniProgramName()));
        values.put("photo_urls", jsonPhotoUrls(request.getPhotoUrls()));
        values.put("description", normalizeNullable(request.getDescription()));
        values.put("latitude", request.getLatitude());
        values.put("longitude", request.getLongitude());
        values.put("map_source", normalizeNullable(request.getMapSource()));
        values.put("surface_type", normalizeNullable(request.getSurfaceType()));
        values.put("has_indoor", request.getHasIndoor());
        values.put("has_outdoor", request.getHasOutdoor());
        values.put("indoor_outdoor", indoorOutdoorValue(request.getHasIndoor(), request.getHasOutdoor()));
        values.put("total_court_count", courtCountValue(request.getTotalCourtCount()));
        values.put("opening_time", timeValue(request.getOpeningTime(), "营业开始时间"));
        values.put("closing_time", timeValue(request.getClosingTime(), "营业结束时间"));
        return values;
    }

    private void assertCourtContentSafe(Map<String, Object> values) {
        contentSafetyService.assertSafeText((String) values.get("name"), "球场名字");
        if (StringUtils.hasText((String) values.get("address"))) {
            contentSafetyService.assertSafeText((String) values.get("address"), "球场地址");
        }
        contentSafetyService.assertSafeText((String) values.get("description"), "球场描述");
    }

    private String jsonPhotoUrls(List<String> photoUrls) {
        List<String> normalized = photoUrls == null ? List.of() : photoUrls.stream()
                .map(this::normalizeNullable)
                .filter(Objects::nonNull)
                .limit(5)
                .toList();
        try {
            return objectMapper.writeValueAsString(normalized);
        } catch (JsonProcessingException e) {
            throw new BusinessException(10001, "球场照片格式错误");
        }
    }

    private List<String> findStrengthTags(String profileId) {
        return mobileProfileMapper.findActiveStrengthTags(profileId);
    }

    private String buildProfileNote(String intro, List<String> tags) {
        if (StringUtils.hasText(intro)) {
            return intro;
        }
        if (tags != null && !tags.isEmpty()) {
            return "标签：" + String.join("、", tags);
        }
        return DEFAULT_INTRO;
    }

    private ProfilePermissionSettingsResponse toPermissionSettingsResponse(Map<String, Object> row) {
        return ProfilePermissionSettingsResponse.builder()
                .genderVisible(row != null && booleanValue(rowValue(row, "gender_visible")))
                .birthdayVisible(row != null && booleanValue(rowValue(row, "birthday_visible")))
                .regionVisible(row != null && booleanValue(rowValue(row, "region_visible")))
                .habitCourtsVisible(row != null && booleanValue(rowValue(row, "habit_courts_visible")))
                .followingListVisible(row != null && booleanValue(rowValue(row, "following_list_visible")))
                .followerListVisible(row != null && booleanValue(rowValue(row, "follower_list_visible")))
                .activityRecordsVisible(row != null && booleanValue(rowValue(row, "activity_records_visible")))
                .build();
    }

    private String buildAvailabilityText(String userId) {
        List<Map<String, Object>> rows = mobileProfileMapper.findAvailabilitySlots(userId);
        if (rows.isEmpty()) {
            return "暂未设置可约时间";
        }
        return rows.stream()
                .map(row -> dayLabel(rowValue(row, "day_of_week")) + " " + timeLabel(rowValue(row, "start_time")) + "-" + timeLabel(rowValue(row, "end_time")))
                .toList()
                .stream()
                .reduce((left, right) -> left + "，" + right)
                .orElse("暂未设置可约时间");
    }

    private String buildAcceptedLevelText(Double ntrpRating) {
        if (ntrpRating == null) {
            return "暂未设置接受等级";
        }
        double min = Math.max(0.5, ntrpRating - 0.5);
        double max = Math.min(7.0, ntrpRating + 0.5);
        return String.format(Locale.US, "NTRP %.1f - %.1f", min, max);
    }

    private String buildMatchPreferenceText(String playPreference, String tennisIdentity) {
        List<String> parts = new ArrayList<>();
        if ("singles".equals(playPreference)) {
            parts.add("单打");
        } else if ("doubles".equals(playPreference)) {
            parts.add("双打");
        } else if ("both".equals(playPreference)) {
            parts.add("单双打都可以");
        } else {
            parts.add("打球偏好未设置");
        }
        if ("coach".equals(tennisIdentity)) {
            parts.add("教练");
        } else if ("professional".equals(tennisIdentity)) {
            parts.add("职业");
        } else if ("coCoach".equals(tennisIdentity)) {
            parts.add("陪练");
        }
        return String.join("、", parts);
    }

    private String dayLabel(Object value) {
        int day = value instanceof Number number ? number.intValue() : 0;
        return switch (day) {
            case 1 -> "周一";
            case 2 -> "周二";
            case 3 -> "周三";
            case 4 -> "周四";
            case 5 -> "周五";
            case 6 -> "周六";
            case 7 -> "周日";
            default -> "未设置";
        };
    }

    private String timeLabel(Object value) {
        if (value instanceof LocalTime time) {
            return "%02d:%02d".formatted(time.getHour(), time.getMinute());
        }
        if (value instanceof java.sql.Time time) {
            LocalTime localTime = time.toLocalTime();
            return "%02d:%02d".formatted(localTime.getHour(), localTime.getMinute());
        }
        return "00:00";
    }

    private List<String> normalizeTags(List<String> tags) {
        if (tags == null) {
            return List.of();
        }
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String tag : tags) {
            String trimmed = normalizeNullable(tag);
            if (trimmed != null) {
                normalized.add(trimmed);
            }
        }
        if (normalized.size() > 10) {
            throw new BusinessException(10001, "标签最多10个");
        }
        return List.copyOf(normalized);
    }

    private void assertEditQuota(String userId, String fieldName, int limit, int days) {
        int count = mobileProfileMapper.countEditLogsSince(userId, fieldName, LocalDateTime.now().minusDays(days));
        if (count >= limit) {
            log.error("Profile edit quota exceeded: userId={}, field={}, count={}, limit={}", userId, fieldName, count, limit);
            throw new BusinessException(12002, "修改次数已达上限");
        }
    }

    private void writeEditLog(String userId, String fieldName) {
        mobileProfileMapper.insertEditLog(UUID.randomUUID().toString(), userId, fieldName);
    }

    private void tryWriteEditLog(String userId, String fieldName, String scene) {
        try {
            writeEditLog(userId, fieldName);
        } catch (DataAccessException e) {
            logDataAccessFailure("Profile edit audit log DB write failed", e,
                    "scene", scene,
                    "userId", userId,
                    "fieldName", fieldName);
        }
    }

    private void logDataAccessFailure(String message, DataAccessException error, Object... context) {
        Throwable root = rootCause(error);
        SQLException sqlException = findSqlException(error);
        log.error("{}: context={}, exceptionClass={}, exceptionMessage={}, rootClass={}, rootMessage={}, sqlState={}, vendorErrorCode={}",
                message,
                contextMap(context),
                error.getClass().getName(),
                error.getMessage(),
                root == null ? null : root.getClass().getName(),
                root == null ? null : root.getMessage(),
                sqlException == null ? null : sqlException.getSQLState(),
                sqlException == null ? null : sqlException.getErrorCode(),
                error);
    }

    private Map<String, Object> contextMap(Object... context) {
        Map<String, Object> values = new LinkedHashMap<>();
        if (context == null) {
            return values;
        }
        for (int i = 0; i + 1 < context.length; i += 2) {
            values.put(String.valueOf(context[i]), context[i + 1]);
        }
        return values;
    }

    private Throwable rootCause(Throwable error) {
        Throwable current = error;
        while (current != null && current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private SQLException findSqlException(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof SQLException sqlException) {
                return sqlException;
            }
            current = current.getCause();
        }
        return null;
    }

    private String normalize(String value) {
        String trimmed = normalizeNullable(value);
        if (trimmed == null) {
            throw new BusinessException(10001, "参数不能为空");
        }
        return trimmed;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeAvatarValue(String value) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            return null;
        }
        String systemAvatarName = normalized
                .replace("irallyin://avatar/", "")
                .replace("avatar:", "")
                .trim();
        if (SYSTEM_AVATAR_NAMES.contains(systemAvatarName)) {
            return systemAvatarName;
        }
        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            return normalized;
        }
        throw new BusinessException(10001, "头像名称不正确");
    }

    private Double numberAsDouble(Object value, Object fallback) {
        Object source = value != null ? value : fallback;
        if (source instanceof Number number) {
            return number.doubleValue();
        }
        return null;
    }

    private Integer integerValue(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return null;
    }

    private Integer positiveIntegerOrNull(Integer value, String fieldName) {
        if (value == null) {
            return null;
        }
        if (value <= 0) {
            throw new BusinessException(10001, fieldName + "必须大于0");
        }
        return value;
    }

    private Double positiveDoubleOrNull(Double value, String fieldName) {
        if (value == null) {
            return null;
        }
        if (value <= 0) {
            throw new BusinessException(10001, fieldName + "必须大于0");
        }
        return value;
    }

    private LocalTime localTimeValue(Object value) {
        if (value instanceof LocalTime localTime) {
            return localTime;
        }
        if (value instanceof java.sql.Time time) {
            return time.toLocalTime();
        }
        if (value instanceof String text && StringUtils.hasText(text)) {
            return LocalTime.parse(text);
        }
        return null;
    }

    private LocalDateTime localDateTimeValue(Object value) {
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        if (value instanceof java.sql.Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        if (value instanceof String text && StringUtils.hasText(text)) {
            return LocalDateTime.parse(text.replace(" ", "T"));
        }
        return null;
    }

    private LocalDateTime parseStartedAt(String value) {
        String normalized = normalize(value);
        try {
            return LocalDateTime.parse(normalized.replace(" ", "T"));
        } catch (RuntimeException e) {
            throw new BusinessException(10001, "开始时间格式不正确");
        }
    }

    private LocalDateTime parseFitnessDateTime(String value, String fieldName) {
        String normalized = normalize(value);
        try {
            return OffsetDateTime.parse(normalized).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDateTime.parse(normalized.replace(" ", "T"));
            } catch (DateTimeParseException e) {
                throw new BusinessException(10001, fieldName + "格式不正确");
            }
        }
    }

    private LocalDateTime parseNullableFitnessDateTime(String value, String fieldName) {
        String normalized = normalizeNullable(value);
        return normalized == null ? null : parseFitnessDateTime(normalized, fieldName);
    }

    private LocalDate parseFitnessDate(String value) {
        String normalized = normalize(value);
        try {
            return LocalDate.parse(normalized);
        } catch (DateTimeParseException e) {
            throw new BusinessException(10001, "汇总日期格式不正确");
        }
    }

    private Integer courtCountValue(Integer value) {
        if (value == null || value <= 0) {
            return null;
        }
        if (value > 10) {
            throw new BusinessException(10001, "球场片数最多10片");
        }
        return value;
    }

    private LocalTime timeValue(String value, String fieldName) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            return null;
        }
        if (isEndOfDayTime(normalized)) {
            return LocalTime.of(23, 59, 59);
        }
        try {
            return LocalTime.parse(normalized);
        } catch (RuntimeException e) {
            throw new BusinessException(10001, fieldName + "格式不正确");
        }
    }

    private boolean isEndOfDayTime(String value) {
        return "24:00".equals(value) || "24:00:00".equals(value);
    }

    private String indoorOutdoorValue(Boolean hasIndoor, Boolean hasOutdoor) {
        if (Boolean.TRUE.equals(hasIndoor) && Boolean.TRUE.equals(hasOutdoor)) {
            return "both";
        }
        if (Boolean.TRUE.equals(hasIndoor)) {
            return "indoor";
        }
        if (Boolean.TRUE.equals(hasOutdoor)) {
            return "outdoor";
        }
        return null;
    }

    private Boolean booleanValue(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return false;
    }

    private int getFollowingCount(String userId) {
        String redisKey = RedisKeys.followingCount(userId);
        try {
            String cached = stringRedisTemplate.opsForValue().get(redisKey);
            if (StringUtils.hasText(cached)) {
                return Integer.parseInt(cached);
            }
        } catch (Exception e) {
            log.warn("Failed to read following count from Redis for userId={}: {}", userId, e.getMessage());
        }
        return refreshFollowingCountCache(userId);
    }

    private int refreshFollowingCountCache(String userId) {
        int followingCount = mobileProfileMapper.countFollowing(userId);
        try {
            // 长期有效，不设置过期时间；关注关系变化时主动刷新。
            stringRedisTemplate.opsForValue().set(RedisKeys.followingCount(userId), String.valueOf(followingCount));
        } catch (Exception e) {
            log.warn("Failed to write following count to Redis for userId={}: {}", userId, e.getMessage());
        }
        return followingCount;
    }

    private Object rowValue(Map<String, Object> row, String columnName) {
        if (row == null || columnName == null) {
            return null;
        }
        if (row.containsKey(columnName)) {
            return row.get(columnName);
        }
        return row.get(toCamelCase(columnName));
    }

    private String toCamelCase(String columnName) {
        StringBuilder result = new StringBuilder();
        boolean uppercaseNext = false;
        for (char ch : columnName.toCharArray()) {
            if (ch == '_') {
                uppercaseNext = true;
            } else if (uppercaseNext) {
                result.append(Character.toUpperCase(ch));
                uppercaseNext = false;
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    private java.time.LocalDate localDateValue(Object value) {
        if (value instanceof java.time.LocalDate date) {
            return date;
        }
        if (value instanceof java.sql.Date date) {
            return date.toLocalDate();
        }
        return null;
    }
}
