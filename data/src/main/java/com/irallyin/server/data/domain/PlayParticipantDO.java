package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 运动参与者。
 */
@Data
@TableName("play_participants")
public class PlayParticipantDO implements Serializable {
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
     * 关联play_sessions.id。
     */
    private String sessionId;
    /**
     * 参与用户ID(NULL=未注册)。
     */
    private String userId;
    /**
     * 显示名称。
     */
    private String displayName;
    /**
     * 角色。
     */
    private String role;
    /**
     * 比赛阵营。
     */
    private String side;
    /**
     * 当时NTRP等级快照。
     */
    private Double ntrpSnapshot;
    /**
     * 确认状态。
     */
    private String participantStatus;
    /**
     * 确认时间。
     */
    private LocalDateTime confirmedAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
