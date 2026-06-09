package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 技能档案。
 */
@Data
@TableName("player_skill_profiles")
public class PlayerSkillProfileDO implements Serializable {
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
     * 关联ir_auth.users.id。
     */
    private String userId;
    /**
     * 1.0-5.5。
     */
    private Double ntrpRating;
    /**
     * 球员身份。
     */
    private String playerIdentity;
    /**
     * 0.0-1.0 置信度。
     */
    private Double confidenceScore;
    /**
     * self_assessed/coach_verified等。
     */
    private String verifiedLevel;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
    /**
     * 数据库字段 updated_at。
     */
    private LocalDateTime updatedAt;
}
