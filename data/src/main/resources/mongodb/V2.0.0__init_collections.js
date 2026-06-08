// ============================================================
// IRallyIn: MongoDB Collection Initialization
// 4 collections: play_trajectories, stroke_records,
//                session_analytics, activity_timeline
// Run once during deployment
// ============================================================

// Switch to irallyin database
db = db.getSiblingDB('irallyin_activity');

// ============================================================
// 1. play_trajectories - 运动轨迹
// GPS trace, movement heatmap, rally stats, summary metrics
// TTL: 365 days
// ============================================================
db.createCollection('play_trajectories', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['userId', 'sessionId', 'sportType', 'createdAt', 'expiresAt'],
            properties: {
                userId:        { bsonType: 'string', description: '关联ir_auth.users.id' },
                sessionId:     { bsonType: 'string', description: '关联ir_activity.play_sessions.id' },
                sportType:     { bsonType: 'string', description: '运动类型如tennis' },
                tracePoints: {
                    bsonType: 'array',
                    items: {
                        bsonType: 'object',
                        properties: {
                            timestamp:  { bsonType: 'date' },
                            latitude:   { bsonType: 'double' },
                            longitude:  { bsonType: 'double' },
                            altitude:   { bsonType: 'double' },
                            speed:      { bsonType: 'double' },
                            accuracy:   { bsonType: 'double' },
                            heartRate:  { bsonType: 'int' }
                        }
                    }
                },
                heatmapPoints: {
                    bsonType: 'array',
                    items: {
                        bsonType: 'object',
                        required: ['latitude', 'longitude', 'intensity'],
                        properties: {
                            latitude:   { bsonType: 'double' },
                            longitude:  { bsonType: 'double' },
                            intensity:  { bsonType: 'double' }
                        }
                    }
                },
                rallyStats: {
                    bsonType: 'array',
                    items: {
                        bsonType: 'object',
                        properties: {
                            rallyIndex:      { bsonType: 'int' },
                            startTime:       { bsonType: 'date' },
                            endTime:         { bsonType: 'date' },
                            durationSeconds: { bsonType: 'int' },
                            strokeCount:     { bsonType: 'int' },
                            maxSpeed:        { bsonType: 'double' },
                            totalDistance:   { bsonType: 'double' },
                            courtZone:       { bsonType: 'string' }
                        }
                    }
                },
                summaryMetrics: {
                    bsonType: 'object',
                    properties: {
                        totalDistance:  { bsonType: 'double' },
                        maxSpeed:      { bsonType: 'double' },
                        avgSpeed:      { bsonType: 'double' },
                        totalStrokes:  { bsonType: 'int' },
                        totalRallies:  { bsonType: 'int' },
                        activeMinutes: { bsonType: 'int' },
                        calories:      { bsonType: 'int' }
                    }
                },
                createdAt:  { bsonType: 'date' },
                expiresAt:  { bsonType: 'date' }
            }
        }
    }
});

// 索引
db.play_trajectories.createIndex(
    { userId: 1, sessionId: 1 },
    { unique: true, name: 'uk_user_session' }
);
db.play_trajectories.createIndex(
    { userId: 1, createdAt: -1 },
    { name: 'idx_user_created' }
);
db.play_trajectories.createIndex(
    { expiresAt: 1 },
    { expireAfterSeconds: 0, name: 'idx_ttl_365d' }
);

// ============================================================
// 2. stroke_records - 击球记录 (V2 视频分析)
// TTL: 30 days (原始数据量大)
// ============================================================
db.createCollection('stroke_records', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['userId', 'sessionId', 'strokeIndex', 'timestamp', 'strokeType', 'createdAt'],
            properties: {
                userId: {
                    bsonType: 'string',
                    description: '关联ir_auth.users.id'
                },
                sessionId: {
                    bsonType: 'string',
                    description: '关联ir_activity.play_sessions.id'
                },
                strokeIndex:   { bsonType: 'int' },
                timestamp:     { bsonType: 'date' },
                strokeType:    { bsonType: 'string', description: 'forehand/backhand/volley/serve' },
                spinType:      { bsonType: 'string', description: 'topspin/slice/flat' },
                speedKmh:      { bsonType: 'double' },
                courtPosition: {
                    bsonType: 'object',
                    properties: { x: { bsonType: 'double' }, y: { bsonType: 'double' } }
                },
                targetPosition: {
                    bsonType: 'object',
                    properties: { x: { bsonType: 'double' }, y: { bsonType: 'double' } }
                },
                videoFrameUrl: { bsonType: 'string' },
                confidence:    { bsonType: 'double' },
                metadata:      { bsonType: 'object' },
                createdAt:     { bsonType: 'date' }
            }
        }
    }
});

// 索引
db.stroke_records.createIndex(
    { userId: 1, sessionId: 1, strokeIndex: 1 },
    { name: 'idx_user_session_stroke' }
);
db.stroke_records.createIndex(
    { userId: 1, strokeType: 1 },
    { name: 'idx_user_stroke_type' }
);
db.stroke_records.createIndex(
    { createdAt: 1 },
    { expireAfterSeconds: 2592000, name: 'idx_ttl_30d' }
);

// ============================================================
// 3. session_analytics - 运动分析 (每会话一份)
// ============================================================
db.createCollection('session_analytics', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['userId', 'sessionId', 'computedAt', 'createdAt'],
            properties: {
                userId:    { bsonType: 'string' },
                sessionId: { bsonType: 'string' },
                performanceScore: {
                    bsonType: 'object',
                    properties: {
                        overall:     { bsonType: 'double' },
                        consistency: { bsonType: 'double' },
                        power:       { bsonType: 'double' },
                        accuracy:    { bsonType: 'double' },
                        movement:    { bsonType: 'double' }
                    }
                },
                strokeDistribution: {
                    bsonType: 'object',
                    properties: {
                        forehand: { bsonType: 'double' },
                        backhand: { bsonType: 'double' },
                        volley:   { bsonType: 'double' },
                        serve:    { bsonType: 'double' }
                    }
                },
                courtCoverage: {
                    bsonType: 'object',
                    properties: {
                        totalArea:     { bsonType: 'double' },
                        heatmap:       { bsonType: 'string' },
                        favoriteZone:  { bsonType: 'string' }
                    }
                },
                heartRateZones: {
                    bsonType: 'object',
                    properties: {
                        zone1: { bsonType: 'int' },
                        zone2: { bsonType: 'int' },
                        zone3: { bsonType: 'int' },
                        zone4: { bsonType: 'int' },
                        zone5: { bsonType: 'int' }
                    }
                },
                comparisonWithHistory: { bsonType: 'object' },
                aiInsights: {
                    bsonType: 'array',
                    items: { bsonType: 'string' }
                },
                computedAt: { bsonType: 'date' },
                createdAt:  { bsonType: 'date' }
            }
        }
    }
});

// 索引
db.session_analytics.createIndex(
    { userId: 1, sessionId: 1 },
    { unique: true, name: 'uk_user_session' }
);
db.session_analytics.createIndex(
    { userId: 1, computedAt: -1 },
    { name: 'idx_user_computed' }
);

// ============================================================
// 4. activity_timeline - 活动时间线
// TTL: 90 days
// ============================================================
db.createCollection('activity_timeline', {
    validator: {
        $jsonSchema: {
            bsonType: 'object',
            required: ['userId', 'eventType', 'timestamp', 'createdAt'],
            properties: {
                userId: {
                    bsonType: 'string',
                    description: '关联ir_auth.users.id'
                },
                sessionId: {
                    bsonType: 'string',
                    description: '关联ir_activity.play_sessions.id(可选)'
                },
                eventType:  { bsonType: 'string', description: 'play_session_completed等' },
                timestamp:  { bsonType: 'date' },
                summary: {
                    bsonType: 'object',
                    properties: {
                        sessionType: { bsonType: 'string' },
                        duration:    { bsonType: 'int' },
                        opponent:    { bsonType: 'string' },
                        result:      { bsonType: 'string' },
                        courtName:   { bsonType: 'string' }
                    }
                },
                feedPostId:    { bsonType: 'string' },
                privacyLevel:  { bsonType: 'string' },
                createdAt:     { bsonType: 'date' }
            }
        }
    }
});

// 索引
db.activity_timeline.createIndex(
    { userId: 1, timestamp: -1 },
    { name: 'idx_user_timestamp' }
);
db.activity_timeline.createIndex(
    { eventType: 1, timestamp: -1 },
    { name: 'idx_event_timestamp' }
);
db.activity_timeline.createIndex(
    { createdAt: 1 },
    { expireAfterSeconds: 7776000, name: 'idx_ttl_90d' }
);

// ============================================================
// 初始化完成
// ============================================================
print('IRallyIn MongoDB collections initialized successfully.');
print('Collections: play_trajectories, stroke_records, session_analytics, activity_timeline');
