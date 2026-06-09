package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价聚合。
 */
@Data
@TableName("player_review_aggregates")
public class PlayerReviewAggregateDO implements Serializable {
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
     * 被评价用户ID。
     */
    private String userId;
    /**
     * 总评价数。
     */
    private Integer totalReviewCount;
    /**
     * 总评分。
     */
    private Double overallScore;
    /**
     * 球技均分。
     */
    private Double skillAvg;
    /**
     * 体育精神均分。
     */
    private Double sportsmanshipAvg;
    /**
     * 守约可靠均分。
     */
    private Double reliabilityAvg;
    /**
     * 沟通友善均分。
     */
    private Double communicationAvg;
    /**
     * 前5正面标签及计数。
     */
    private String topPositiveTags;
    /**
     * 信任置信度(0-1)。
     */
    private Double trustConfidence;
    /**
     * 数据库字段 updated_at。
     */
    private LocalDateTime updatedAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
