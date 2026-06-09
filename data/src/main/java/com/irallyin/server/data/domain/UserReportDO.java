package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户举报。
 */
@Data
@TableName("user_reports")
public class UserReportDO implements Serializable {
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
     * 举报人ID。
     */
    private String reporterId;
    /**
     * 被举报人ID。
     */
    private String reportedUserId;
    /**
     * 举报类型。
     */
    private String reportType;
    /**
     * 举报描述。
     */
    private String description;
    /**
     * 关联资源类型如feed_post。
     */
    private String resourceType;
    /**
     * 关联资源ID。
     */
    private String resourceId;
    /**
     * 处理状态。
     */
    private String reviewStatus;
    /**
     * 审核管理员ID。
     */
    private String reviewedBy;
    /**
     * 审核时间。
     */
    private LocalDateTime reviewedAt;
    /**
     * 数据库字段 created_at。
     */
    private LocalDateTime createdAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
