package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价风控日志。
 */
@Data
@TableName("review_risk_logs")
public class ReviewRiskLogDO implements Serializable {
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
     * 关联player_reviews.id。
     */
    private String reviewId;
    /**
     * 风险类型: extreme_score/high_frequency/mutual_inflation。
     */
    private String riskType;
    /**
     * 结构化风险详情。
     */
    private String riskDetail;
    /**
     * 数据库字段 created_at。
     */
    private LocalDateTime createdAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
