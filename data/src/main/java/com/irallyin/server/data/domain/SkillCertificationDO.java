package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 认证记录。
 */
@Data
@TableName("skill_certifications")
public class SkillCertificationDO implements Serializable {
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
     * 认证类型。
     */
    private String certType;
    /**
     * 认证状态。
     */
    private String certStatus;
    /**
     * 用户自报等级。
     */
    private Double requestedNtrp;
    /**
     * 认证后等级。
     */
    private Double finalNtrp;
    /**
     * 球员身份。
     */
    private String playerIdentity;
    /**
     * 第三方认证的教练ID。
     */
    private String coachId;
    /**
     * 教练评语(500字)。
     */
    private String coachComment;
    /**
     * V2 AI视频分析结果。
     */
    private String videoAnalysisResult;
    /**
     * 大数据评分详情。
     */
    private String bigDataScore;
    /**
     * 提交时间。
     */
    private LocalDateTime submittedAt;
    /**
     * 认证通过时间。
     */
    private LocalDateTime verifiedAt;
    /**
     * 过期时间。
     */
    private LocalDateTime expiredAt;
    /**
     * 拒绝原因。
     */
    private String rejectedReason;
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
