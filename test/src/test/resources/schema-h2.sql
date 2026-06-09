-- ============================================================
-- IRallyIn: H2-compatible schema for testing
-- Converted from MySQL migration files V1.0.0.01 through V1.2.0.02
-- ============================================================

-- ============================================================
-- ir_auth schema - Authentication & Accounts
-- Source: V1.0.0.01__create_ir_auth_schema.sql
-- ============================================================

-- 1. users
CREATE TABLE users (
    id                    CHAR(36)        NOT NULL,
    phone                 VARCHAR(20)     NULL,
    email                 VARCHAR(255)    NULL,
    password_hash         VARCHAR(255)    NULL,
    display_name          VARCHAR(50)     NOT NULL,
    avatar_url            VARCHAR(512)    NULL,
    bio                   VARCHAR(160)    NULL,
    gender                VARCHAR(20)     NULL,
    birthday              DATE            NULL,
    country               VARCHAR(100)    NULL,
    province              VARCHAR(100)    NULL,
    city                  VARCHAR(100)    NULL,
    district              VARCHAR(100)    NULL,
    real_name_visible     BOOLEAN         NOT NULL DEFAULT FALSE,
    locale                VARCHAR(10)     NOT NULL DEFAULT 'zh-CN',
    timezone              VARCHAR(50)     NOT NULL DEFAULT 'Asia/Shanghai',
    onboarding_completed  BOOLEAN         NOT NULL DEFAULT FALSE,
    onboarding_step       TINYINT         NOT NULL DEFAULT 0,
    player_identity       VARCHAR(20)     NULL,
    ntrp_rating           DECIMAL(3,1)    NULL,
    sys_ntrp_rating       DECIMAL(3,1)    NULL,
    dominant_hand         VARCHAR(20)     NULL,
    access_status         TINYINT         NOT NULL DEFAULT 0,
    status                TINYINT         NOT NULL DEFAULT 0,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at            TIMESTAMP       NULL,
    PRIMARY KEY (id),
    UNIQUE (phone),
    UNIQUE (email)
);

CREATE TABLE profile_edit_audit_logs (
    id                    CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NOT NULL,
    field_name            VARCHAR(40)     NOT NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 2. linked_accounts (includes V1.2.0.02 extensions)
CREATE TABLE linked_accounts (
    id                        CHAR(36)        NOT NULL,
    user_id                   CHAR(36)        NOT NULL,
    provider                  VARCHAR(20)     NOT NULL,
    provider_user_id          VARCHAR(255)    NOT NULL,
    provider_email            VARCHAR(255)    NULL,
    provider_email_verified   BOOLEAN         NOT NULL DEFAULT FALSE,
    provider_nickname         VARCHAR(100)    NULL,
    provider_avatar_url       VARCHAR(512)    NULL,
    provider_locale           VARCHAR(20)     NULL,
    provider_scope            VARCHAR(1000)   NULL,
    provider_link_updated_at  TIMESTAMP       NULL,
    linked_at                 TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at             TIMESTAMP       NULL,
    status                    TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (provider, provider_user_id)
);

CREATE INDEX idx_provider_email ON linked_accounts (provider, provider_email);

-- 3. refresh_tokens
CREATE TABLE refresh_tokens (
    id                    CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NOT NULL,
    token_hash            VARCHAR(128)    NOT NULL,
    device_id             VARCHAR(255)    NULL,
    device_info           VARCHAR(500)    NULL,
    expires_at            TIMESTAMP       NOT NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at            TIMESTAMP       NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (token_hash)
);

-- 4. verification_codes
CREATE TABLE verification_codes (
    id                    CHAR(36)        NOT NULL,
    target                VARCHAR(255)    NOT NULL,
    code_hash             VARCHAR(128)    NOT NULL,
    purpose               VARCHAR(20)     NOT NULL,
    expires_at            TIMESTAMP       NOT NULL,
    used_at               TIMESTAMP       NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address            VARCHAR(45)     NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 5. login_audit_log
CREATE TABLE login_audit_log (
    id                    CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NULL,
    provider              VARCHAR(20)     NOT NULL,
    ip_address            VARCHAR(45)     NOT NULL,
    device_info           VARCHAR(500)    NULL,
    success               BOOLEAN         NOT NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id, created_at)
);

-- ============================================================
-- ir_profile schema - User Profiles
-- Source: V1.0.0.10__create_ir_profile_schema.sql
-- ============================================================

-- 1. player_skill_profiles
CREATE TABLE player_skill_profiles (
    id                    CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NOT NULL,
    ntrp_rating           DECIMAL(3,1)    NOT NULL,
    player_identity       VARCHAR(20)     NOT NULL,
    confidence_score      DECIMAL(5,4)    NOT NULL DEFAULT 0.0000,
    verified_level        VARCHAR(20)     NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    updated_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (user_id)
);

-- 2. skill_certifications
CREATE TABLE skill_certifications (
    id                    CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NOT NULL,
    cert_type             VARCHAR(20)     NOT NULL,
    cert_status           VARCHAR(20)     NOT NULL DEFAULT 'draft',
    requested_ntrp        DECIMAL(3,1)    NULL,
    final_ntrp            DECIMAL(3,1)    NULL,
    player_identity       VARCHAR(20)     NULL,
    coach_id              CHAR(36)        NULL,
    coach_comment         TEXT            NULL,
    video_analysis_result CLOB            NULL,
    big_data_score        CLOB            NULL,
    submitted_at          TIMESTAMP       NULL,
    verified_at           TIMESTAMP       NULL,
    expired_at            TIMESTAMP       NULL,
    rejected_reason       VARCHAR(500)    NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 3. cert_photos
CREATE TABLE cert_photos (
    id                    CHAR(36)        NOT NULL,
    certification_id      CHAR(36)        NOT NULL,
    photo_url             VARCHAR(512)    NOT NULL,
    thumbnail_url         VARCHAR(512)    NULL,
    display_order         TINYINT         NOT NULL DEFAULT 0,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 4. skill_videos
CREATE TABLE skill_videos (
    id                    CHAR(36)        NOT NULL,
    certification_id      CHAR(36)        NOT NULL,
    video_url             VARCHAR(512)    NOT NULL,
    thumbnail_url         VARCHAR(512)    NULL,
    duration_seconds      SMALLINT        NULL,
    file_size_mb          DECIMAL(6,1)    NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (certification_id)
);

-- 5. playing_habits
CREATE TABLE playing_habits (
    id                    CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NOT NULL,
    monthly_freq_min      TINYINT         NULL,
    monthly_freq_max      TINYINT         NULL,
    court_surface_pref    VARCHAR(30)     NULL,
    indoor_outdoor_pref   VARCHAR(20)     NULL,
    play_preference       VARCHAR(20)     NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (user_id)
);

-- 6. habit_cities
CREATE TABLE habit_cities (
    id                    CHAR(36)        NOT NULL,
    habit_id              CHAR(36)        NOT NULL,
    city_code             VARCHAR(10)     NOT NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (habit_id, city_code)
);

-- 7. habit_districts
CREATE TABLE habit_districts (
    id                    CHAR(36)        NOT NULL,
    habit_id              CHAR(36)        NOT NULL,
    district_code         VARCHAR(10)     NOT NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (habit_id, district_code)
);

-- 8. habit_courts
CREATE TABLE habit_courts (
    id                    CHAR(36)        NOT NULL,
    habit_id              CHAR(36)        NOT NULL,
    court_id              CHAR(36)        NOT NULL,
    is_primary            BOOLEAN         NOT NULL DEFAULT FALSE,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (habit_id, court_id)
);

-- 9. habit_weekly_slots
CREATE TABLE habit_weekly_slots (
    id                    CHAR(36)        NOT NULL,
    habit_id              CHAR(36)        NOT NULL,
    day_of_week           TINYINT         NOT NULL,
    start_time            TIME            NOT NULL,
    end_time              TIME            NOT NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT chk_time_range CHECK (end_time > start_time)
);

-- 10. schedule_overrides
CREATE TABLE schedule_overrides (
    id                    CHAR(36)        NOT NULL,
    habit_id              CHAR(36)        NOT NULL,
    date                  DATE            NOT NULL,
    is_available          BOOLEAN         NOT NULL,
    note                  VARCHAR(200)    NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (habit_id, date)
);

-- 11. tennis_profiles
CREATE TABLE tennis_profiles (
    id                    CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NOT NULL,
    play_preference       VARCHAR(20)     NULL,
    backhand_type         VARCHAR(20)     NULL,
    playing_style         VARCHAR(20)     NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (user_id)
);

-- 12. tennis_profile_idols
CREATE TABLE tennis_profile_idols (
    id                    CHAR(36)        NOT NULL,
    profile_id            CHAR(36)        NOT NULL,
    tennis_star_id        CHAR(36)        NULL,
    custom_name           VARCHAR(50)     NULL,
    display_order         TINYINT         NOT NULL DEFAULT 0,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT chk_idol_source CHECK (tennis_star_id IS NOT NULL OR custom_name IS NOT NULL)
);

-- 13. tennis_profile_serve_types
CREATE TABLE tennis_profile_serve_types (
    id                    CHAR(36)        NOT NULL,
    profile_id            CHAR(36)        NOT NULL,
    serve_type            VARCHAR(20)     NOT NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (profile_id, serve_type)
);

-- 14. tennis_profile_strength_tags
CREATE TABLE tennis_profile_strength_tags (
    id                    CHAR(36)        NOT NULL,
    profile_id            CHAR(36)        NOT NULL,
    tag                   VARCHAR(30)     NOT NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (profile_id, tag)
);

-- 15. tennis_profile_improvement_tags
CREATE TABLE tennis_profile_improvement_tags (
    id                    CHAR(36)        NOT NULL,
    profile_id            CHAR(36)        NOT NULL,
    tag                   VARCHAR(30)     NOT NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (profile_id, tag)
);

-- 16. equipment_bags
CREATE TABLE equipment_bags (
    id                    CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NOT NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (user_id)
);

-- 17. rackets
CREATE TABLE rackets (
    id                    CHAR(36)        NOT NULL,
    bag_id                CHAR(36)        NOT NULL,
    brand                 VARCHAR(50)     NOT NULL,
    model                 VARCHAR(100)    NOT NULL,
    grip_size             VARCHAR(10)     NULL,
    weight_gram           SMALLINT        NULL,
    head_size_sq_in       SMALLINT        NULL,
    image_url             VARCHAR(512)    NULL,
    is_primary            BOOLEAN         NOT NULL DEFAULT FALSE,
    display_order         TINYINT         NOT NULL DEFAULT 0,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 18. tennis_shoes
CREATE TABLE tennis_shoes (
    id                    CHAR(36)        NOT NULL,
    bag_id                CHAR(36)        NOT NULL,
    brand                 VARCHAR(50)     NOT NULL,
    model                 VARCHAR(100)    NOT NULL,
    size                  VARCHAR(10)     NULL,
    court_type            VARCHAR(20)     NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 19. tennis_string_setups
CREATE TABLE tennis_string_setups (
    id                    CHAR(36)        NOT NULL,
    racket_id             CHAR(36)        NULL,
    main_string_brand     VARCHAR(50)     NULL,
    cross_string_brand    VARCHAR(50)     NULL,
    string_type           VARCHAR(20)     NULL,
    main_tension_lbs      DECIMAL(4,1)    NULL,
    cross_tension_lbs     DECIMAL(4,1)    NULL,
    strung_at             DATE            NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 20. cities
CREATE TABLE cities (
    code                  VARCHAR(10)     NOT NULL,
    name                  VARCHAR(50)     NOT NULL,
    english_name          VARCHAR(50)     NULL,
    country_code          CHAR(2)         NOT NULL,
    latitude              DECIMAL(9,6)    NULL,
    longitude             DECIMAL(9,6)    NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (code)
);

-- 21. areas
CREATE TABLE areas (
    code                  VARCHAR(10)     NOT NULL,
    city_code             VARCHAR(10)     NOT NULL,
    name                  VARCHAR(50)     NOT NULL,
    english_name          VARCHAR(50)     NULL,
    latitude              DECIMAL(9,6)    NULL,
    longitude             DECIMAL(9,6)    NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (code)
);

-- 22. courts
CREATE TABLE courts (
    id                    CHAR(36)        NOT NULL,
    country               VARCHAR(100)    NULL,
    city                  VARCHAR(100)    NULL,
    area_code             VARCHAR(10)     NULL,
    name                  VARCHAR(200)    NOT NULL,
    address               VARCHAR(500)    NULL,
    latitude              DECIMAL(9,6)    NULL,
    longitude             DECIMAL(9,6)    NULL,
    surface_type          VARCHAR(30)     NULL,
    indoor_outdoor        VARCHAR(20)     NULL,
    has_indoor            BOOLEAN         NOT NULL DEFAULT FALSE,
    has_outdoor           BOOLEAN         NOT NULL DEFAULT FALSE,
    total_court_count     INT             NULL,
    indoor_court_count    INT             NULL,
    outdoor_court_count   INT             NULL,
    contact_phone         VARCHAR(20)     NULL,
    wechat_mini_program_name VARCHAR(100) NULL,
    photo_urls            CLOB            NULL,
    description           CLOB            NULL,
    created_by            CHAR(36)        NULL,
    amenities             CLOB            NULL,
    venue_status          VARCHAR(20)     NOT NULL DEFAULT 'active',
    approval_status       VARCHAR(20)     NOT NULL DEFAULT 'approved',
    operator_managed      BOOLEAN         NOT NULL DEFAULT FALSE,
    reviewed_by           CHAR(36)        NULL,
    reviewed_at           TIMESTAMP       NULL,
    rejected_reason       VARCHAR(500)    NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (name)
);

-- 23. tennis_stars
CREATE TABLE tennis_stars (
    id                    CHAR(36)        NOT NULL,
    name                  VARCHAR(100)    NOT NULL,
    country               CHAR(2)         NULL,
    avatar_url            VARCHAR(512)    NULL,
    active_status         VARCHAR(20)     NOT NULL DEFAULT 'active',
    handedness            VARCHAR(20)     NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- ============================================================
-- ir_privacy schema - Privacy & Permissions
-- Source: V1.0.0.20__create_ir_privacy_schema.sql
-- ============================================================

-- 1. privacy_settings
CREATE TABLE privacy_settings (
    id                    CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NOT NULL,
    category              VARCHAR(20)     NOT NULL,
    visibility            VARCHAR(20)     NOT NULL DEFAULT 'matchedPlayers',
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (user_id, category)
);

-- 2. resource_privacy
CREATE TABLE resource_privacy (
    id                    CHAR(36)        NOT NULL,
    owner_id              CHAR(36)        NOT NULL,
    resource_type         VARCHAR(30)     NOT NULL,
    resource_id           CHAR(36)        NOT NULL,
    visibility            VARCHAR(20)     NOT NULL,
    allowed_club_ids      CLOB            NULL,
    allowed_user_ids      CLOB            NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (resource_type, resource_id)
);

-- 3. block_relations
CREATE TABLE block_relations (
    id                    CHAR(36)        NOT NULL,
    blocker_id            CHAR(36)        NOT NULL,
    blocked_user_id       CHAR(36)        NOT NULL,
    reason                VARCHAR(200)    NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (blocker_id, blocked_user_id)
);

-- 4. user_reports
CREATE TABLE user_reports (
    id                    CHAR(36)        NOT NULL,
    reporter_id           CHAR(36)        NOT NULL,
    reported_user_id      CHAR(36)        NOT NULL,
    report_type           VARCHAR(30)     NOT NULL,
    description           TEXT            NULL,
    resource_type         VARCHAR(30)     NULL,
    resource_id           CHAR(36)        NULL,
    review_status         VARCHAR(20)     NOT NULL DEFAULT 'pending',
    reviewed_by           CHAR(36)        NULL,
    reviewed_at           TIMESTAMP       NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 5. account_deletion_requests
CREATE TABLE account_deletion_requests (
    id                    CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NOT NULL,
    reason                TEXT            NULL,
    request_status        VARCHAR(20)     NOT NULL DEFAULT 'pending',
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at          TIMESTAMP       NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- ============================================================
-- ir_social schema - Social Content
-- Source: V1.1.0.01__create_ir_social_schema.sql
-- ============================================================

-- 1. feed_posts
CREATE TABLE feed_posts (
    id                    CHAR(36)        NOT NULL,
    author_id             CHAR(36)        NOT NULL,
    text                  TEXT            NULL,
    image_urls            CLOB            NULL,
    play_session_id       CHAR(36)        NULL,
    court_id              CHAR(36)        NULL,
    topic_tags            CLOB            NULL,
    visibility            VARCHAR(20)     NOT NULL DEFAULT 'matchedPlayers',
    like_count            INT             NOT NULL DEFAULT 0,
    comment_count         INT             NOT NULL DEFAULT 0,
    status                TINYINT         NOT NULL DEFAULT 0,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, created_at)
);

-- 2. feed_comments
CREATE TABLE feed_comments (
    id                    CHAR(36)        NOT NULL,
    post_id               CHAR(36)        NOT NULL,
    author_id             CHAR(36)        NOT NULL,
    content               VARCHAR(300)    NOT NULL,
    parent_comment_id     CHAR(36)        NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, created_at)
);

-- 3. feed_likes
CREATE TABLE feed_likes (
    id                    CHAR(36)        NOT NULL,
    post_id               CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NOT NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id, created_at),
    UNIQUE (post_id, user_id, created_at)
);

-- 4. feed_media
CREATE TABLE feed_media (
    id                    CHAR(36)        NOT NULL,
    post_id               CHAR(36)        NOT NULL,
    media_url             VARCHAR(512)    NOT NULL,
    media_type            VARCHAR(20)     NOT NULL,
    display_order         TINYINT         NOT NULL DEFAULT 0,
    thumbnail_url         VARCHAR(512)    NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- ============================================================
-- ir_activity schema - Activity History & Relationship Network
-- Source: V1.1.0.10__create_ir_activity_schema.sql
-- ============================================================

-- 1. play_sessions
CREATE TABLE play_sessions (
    id                    CHAR(36)        NOT NULL,
    owner_id              CHAR(36)        NOT NULL,
    sport_type            VARCHAR(20)     NOT NULL DEFAULT 'tennis',
    session_type          VARCHAR(20)     NOT NULL,
    title                 VARCHAR(100)    NULL,
    started_at            TIMESTAMP       NOT NULL,
    ended_at              TIMESTAMP       NULL,
    duration_minutes      SMALLINT        NULL,
    city_code             VARCHAR(10)     NULL,
    district_code         VARCHAR(10)     NULL,
    court_id              CHAR(36)        NULL,
    court_name            VARCHAR(200)    NULL,
    score_summary         CLOB            NULL,
    notes                 TEXT            NULL,
    privacy_level         VARCHAR(20)     NOT NULL DEFAULT 'matchedPlayers',
    status                TINYINT         NOT NULL DEFAULT 0,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, started_at)
);

-- 2. play_participants
CREATE TABLE play_participants (
    id                    CHAR(36)        NOT NULL,
    session_id            CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NULL,
    display_name          VARCHAR(50)     NOT NULL,
    role                  VARCHAR(20)     NOT NULL,
    side                  VARCHAR(20)     NULL,
    ntrp_snapshot         DECIMAL(3,1)    NULL,
    participant_status    VARCHAR(20)     NOT NULL DEFAULT 'pending',
    confirmed_at          TIMESTAMP       NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 3. player_relationship_edges
CREATE TABLE player_relationship_edges (
    id                        CHAR(36)        NOT NULL,
    user_id                   CHAR(36)        NOT NULL,
    peer_user_id              CHAR(36)        NOT NULL,
    total_sessions            INT             NOT NULL DEFAULT 0,
    last_played_at            TIMESTAMP       NULL,
    most_common_session_type  VARCHAR(20)     NULL,
    relationship_strength     DECIMAL(5,2)    NOT NULL DEFAULT 0.00,
    updated_at                TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                    TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (user_id, peer_user_id)
);

-- ============================================================
-- ir_review schema - Player Reviews
-- Source: V1.1.0.20__create_ir_review_schema.sql
-- ============================================================

-- 1. player_reviews
CREATE TABLE player_reviews (
    id                    CHAR(36)        NOT NULL,
    play_session_id       CHAR(36)        NULL,
    reviewer_id           CHAR(36)        NOT NULL,
    reviewee_id           CHAR(36)        NOT NULL,
    skill_score           TINYINT         NOT NULL,
    sportsmanship_score   TINYINT         NOT NULL,
    reliability_score     TINYINT         NOT NULL,
    communication_score   TINYINT         NOT NULL,
    overall_score         DECIMAL(3,1)    NOT NULL,
    tags                  CLOB            NULL,
    private_note          TEXT            NULL,
    moderation_status     VARCHAR(20)     NOT NULL DEFAULT 'active',
    is_verified           BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (reviewer_id, play_session_id, reviewee_id)
);

-- 2. player_review_aggregates
CREATE TABLE player_review_aggregates (
    id                    CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NOT NULL,
    total_review_count    INT             NOT NULL DEFAULT 0,
    overall_score         DECIMAL(3,2)    NOT NULL DEFAULT 0.00,
    skill_avg             DECIMAL(3,2)    NOT NULL DEFAULT 0.00,
    sportsmanship_avg     DECIMAL(3,2)    NOT NULL DEFAULT 0.00,
    reliability_avg       DECIMAL(3,2)    NOT NULL DEFAULT 0.00,
    communication_avg     DECIMAL(3,2)    NOT NULL DEFAULT 0.00,
    top_positive_tags     CLOB            NULL,
    trust_confidence      DECIMAL(5,4)    NOT NULL DEFAULT 0.0000,
    updated_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (user_id)
);

-- 3. review_risk_logs
CREATE TABLE review_risk_logs (
    id                    CHAR(36)        NOT NULL,
    review_id             CHAR(36)        NOT NULL,
    risk_type             VARCHAR(30)     NOT NULL,
    risk_detail           CLOB            NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- ============================================================
-- ir_club schema - Clubs
-- Source: V1.2.0.01__create_ir_club_schema.sql
-- ============================================================

-- 1. clubs
CREATE TABLE clubs (
    id                    CHAR(36)        NOT NULL,
    name                  VARCHAR(50)     NOT NULL,
    english_name          VARCHAR(50)     NULL,
    city_code             VARCHAR(10)     NULL,
    description           TEXT            NULL,
    cover_image_url       VARCHAR(512)    NULL,
    owner_id              CHAR(36)        NOT NULL,
    join_policy           VARCHAR(20)     NOT NULL DEFAULT 'approval',
    member_count          INT             NOT NULL DEFAULT 0,
    club_status           VARCHAR(20)     NOT NULL DEFAULT 'active',
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (name, city_code)
);

-- 2. club_members
CREATE TABLE club_members (
    id                    CHAR(36)        NOT NULL,
    club_id               CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NOT NULL,
    role                  VARCHAR(20)     NOT NULL DEFAULT 'member',
    member_status         VARCHAR(20)     NOT NULL DEFAULT 'active',
    joined_at             TIMESTAMP       NULL,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (club_id, user_id)
);

-- 3. club_join_requests
CREATE TABLE club_join_requests (
    id                    CHAR(36)        NOT NULL,
    club_id               CHAR(36)        NOT NULL,
    user_id               CHAR(36)        NOT NULL,
    message               TEXT            NULL,
    request_status        VARCHAR(20)     NOT NULL DEFAULT 'pending',
    reviewed_by           CHAR(36)        NULL,
    reviewed_at           TIMESTAMP       NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 4. club_events
CREATE TABLE club_events (
    id                    CHAR(36)        NOT NULL,
    club_id               CHAR(36)        NOT NULL,
    organizer_id          CHAR(36)        NOT NULL,
    title                 VARCHAR(100)    NOT NULL,
    court_id              CHAR(36)        NULL,
    court_name            VARCHAR(200)    NULL,
    started_at            TIMESTAMP       NOT NULL,
    ended_at              TIMESTAMP       NULL,
    max_participants      SMALLINT        NULL,
    event_status          VARCHAR(20)     NOT NULL DEFAULT 'upcoming',
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                TINYINT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
