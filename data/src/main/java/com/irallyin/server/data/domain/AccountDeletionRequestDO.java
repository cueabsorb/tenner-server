package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 账号注销请求。
 */
@Data
@TableName("account_deletion_requests")
public class AccountDeletionRequestDO implements Serializable {
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
     * 申请注销的用户ID。
     */
    private String userId;
    /**
     * 注销原因。
     */
    private String reason;
    /**
     * 注销请求状态。
     */
    private String requestStatus;
    /**
     * 数据库字段 created_at。
     */
    private LocalDateTime createdAt;
    /**
     * 处理完成时间。
     */
    private LocalDateTime processedAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
