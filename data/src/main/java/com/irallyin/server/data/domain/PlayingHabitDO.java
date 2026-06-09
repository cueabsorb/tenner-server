package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 打球习惯。
 */
@Data
@TableName("playing_habits")
public class PlayingHabitDO implements Serializable {
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
     * 关联用户ID。
     */
    private String userId;
    /**
     * 每月最低打球次数。
     */
    private Integer monthlyFreqMin;
    /**
     * 每月最高打球次数。
     */
    private Integer monthlyFreqMax;
    /**
     * 偏好场地类型(多选)。
     */
    private String courtSurfacePref;
    /**
     * 室内外偏好。
     */
    private String indoorOutdoorPref;
    /**
     * 单双打偏好。
     */
    private String playPreference;
    /**
     * 数据库字段 created_at。
     */
    private LocalDateTime createdAt;
    /**
     * 数据库字段 updated_at。
     */
    private LocalDateTime updatedAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
