package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 俱乐部成员。
 */
@Data
@TableName("club_members")
public class ClubMemberDO implements Serializable {
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
     * 关联用户ID。
     */
    private String userId;
    /**
     * 角色。
     */
    private String role;
    /**
     * 成员状态。
     */
    private String memberStatus;
    /**
     * 加入时间。
     */
    private LocalDateTime joinedAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
