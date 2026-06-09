package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 球友关系边。
 */
@Data
@TableName("player_relationship_edges")
public class PlayerRelationshipEdgeDO implements Serializable {
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
     * 用户ID。
     */
    private String userId;
    /**
     * 球友ID。
     */
    private String peerUserId;
    /**
     * 共同打球次数。
     */
    private Integer totalSessions;
    /**
     * 最后一起打球时间。
     */
    private LocalDateTime lastPlayedAt;
    /**
     * 最常见场次类型。
     */
    private String mostCommonSessionType;
    /**
     * 关系强度(0-100)。
     */
    private Double relationshipStrength;
    /**
     * 数据库字段 updated_at。
     */
    private LocalDateTime updatedAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
