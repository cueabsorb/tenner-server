package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 运动记录。
 */
@Data
@TableName("play_sessions")
public class PlaySessionDO implements Serializable {
    /**
     * 序列化版本号。
     */
    private static final long serialVersionUID = 1L;

    /**
     * UUID v4。
     */
    private String id;
    /**
     * 记录创建者ID。
     */
    private String ownerId;
    /**
     * 运动类型。
     */
    private String sportType;
    /**
     * 场次类型。
     */
    private String sessionType;
    /**
     * 标题。
     */
    private String title;
    /**
     * 开始时间。
     */
    private LocalDateTime startedAt;
    /**
     * 结束时间。
     */
    private LocalDateTime endedAt;
    /**
     * 持续分钟数。
     */
    private Integer durationMinutes;
    /**
     * 城市编码。
     */
    private String cityCode;
    /**
     * 区域编码。
     */
    private String districtCode;
    /**
     * 球场ID。
     */
    private String courtId;
    /**
     * 球场名称(冗余)。
     */
    private String courtName;
    /**
     * 比赛比分详情。
     */
    private String scoreSummary;
    /**
     * 备注。
     */
    private String notes;
    /**
     * 隐私级别。
     */
    private String privacyLevel;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
    /**
     * 数据库字段 created_at。
     */
    private LocalDateTime createdAt;
    /**
     * 数据库字段 updated_at。
     */
    private LocalDateTime updatedAt;
}
