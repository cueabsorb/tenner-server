package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 球员评价。
 */
@Data
@TableName("player_reviews")
public class PlayerReviewDO implements Serializable {
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
     * 关联运动记录ID(NULL=无共同记录)。
     */
    private String playSessionId;
    /**
     * 评价者ID。
     */
    private String reviewerId;
    /**
     * 被评价者ID。
     */
    private String revieweeId;
    /**
     * 球技评分(1-5)。
     */
    private Integer skillScore;
    /**
     * 体育精神评分(1-5)。
     */
    private Integer sportsmanshipScore;
    /**
     * 守约可靠评分(1-5)。
     */
    private Integer reliabilityScore;
    /**
     * 沟通友善评分(1-5)。
     */
    private Integer communicationScore;
    /**
     * 加权总分。
     */
    private Double overallScore;
    /**
     * 评价标签数组。
     */
    private String tags;
    /**
     * 私密备注(仅评价者可见)。
     */
    private String privateNote;
    /**
     * 内容审核状态。
     */
    private String moderationStatus;
    /**
     * 是否绑定已确认运动记录。
     */
    private Boolean isVerified;
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
