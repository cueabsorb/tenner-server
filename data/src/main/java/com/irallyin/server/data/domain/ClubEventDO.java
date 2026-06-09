package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 俱乐部活动。
 */
@Data
@TableName("club_events")
public class ClubEventDO implements Serializable {
    /**
     * 序列化版本号。
     */
    private static final long serialVersionUID = 1L;

    /**
     * UUID v4。
     */
    @TableId
    private String id;
    /**
     * 关联clubs.id。
     */
    private String clubId;
    /**
     * 组织者ID。
     */
    private String organizerId;
    /**
     * 活动标题。
     */
    private String title;
    /**
     * 球场ID。
     */
    private String courtId;
    /**
     * 球场名称(冗余)。
     */
    private String courtName;
    /**
     * 开始时间。
     */
    private LocalDateTime startedAt;
    /**
     * 结束时间。
     */
    private LocalDateTime endedAt;
    /**
     * 最大参与人数。
     */
    private Integer maxParticipants;
    /**
     * 活动状态。
     */
    private String eventStatus;
    /**
     * 数据库字段 created_at。
     */
    private LocalDateTime createdAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
