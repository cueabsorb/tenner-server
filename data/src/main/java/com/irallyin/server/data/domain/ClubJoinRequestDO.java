package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 入会申请。
 */
@Data
@TableName("club_join_requests")
public class ClubJoinRequestDO implements Serializable {
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
     * 申请用户ID。
     */
    private String userId;
    /**
     * 申请留言。
     */
    private String message;
    /**
     * 申请状态。
     */
    private String requestStatus;
    /**
     * 审核人ID。
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
