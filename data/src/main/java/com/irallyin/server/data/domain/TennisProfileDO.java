package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 网球兴趣档案。
 */
@Data
@TableName("tennis_profiles")
public class TennisProfileDO implements Serializable {
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
     * 单双打偏好。
     */
    private String playPreference;
    /**
     * 反手类型。
     */
    private String backhandType;
    /**
     * 打法风格。
     */
    private String playingStyle;
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
