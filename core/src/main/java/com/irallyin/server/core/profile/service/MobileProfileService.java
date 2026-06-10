package com.irallyin.server.core.profile.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irallyin.server.common.exception.BusinessException;
import com.irallyin.server.core.profile.dto.*;
import com.irallyin.server.data.domain.CourtDO;
import com.irallyin.server.data.mapper.MobileProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MobileProfileService {
    private static final String DEFAULT_INTRO = "因热爱而相聚，为梦想而挥拍";

    private final MobileProfileMapper mobileProfileMapper;
    private final ProfileContentSafetyService contentSafetyService;
    private final ObjectMapper objectMapper;

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
                .habitCourts(findHabitCourts(userId))
                .followingCount(mobileProfileMapper.countFollowing(userId))
                .followerCount(mobileProfileMapper.countFollowers(userId))
                .receivedLikeCount(mobileProfileMapper.sumReceivedLikes(userId))
                .build();
    }

    @Transactional
    public MobileProfileResponse updateAvatar(String userId, ProfileAvatarUpdateRequest request) {
        String avatarUrl = normalizeNullable(request.getAvatarUrl());
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
        String normalizedKeyword = normalizeNullable(keyword);
        if (!StringUtils.hasText(normalizedKeyword)) {
            return List.of();
        }

        Map<String, UserSearchResponse.UserSearchResponseBuilder> builders = new LinkedHashMap<>();
        Map<String, List<String>> courtNames = new LinkedHashMap<>();
        for (Map<String, Object> row : mobileProfileMapper.searchUsers(normalizedKeyword)) {
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
                .limit(100)
                .map(entry -> entry.getValue()
                        .habitCourts(courtNames.getOrDefault(entry.getKey(), List.of()))
                        .build())
                .toList();
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
