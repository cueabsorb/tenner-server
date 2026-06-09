package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 资源级可见性。
 */
@Data
@TableName("resource_privacy")
public class ResourcePrivacyDO implements Serializable {
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
     * 资源所有者ID。
     */
    private String ownerId;
    /**
     * 资源类型如feed_post/play_session。
     */
    private String resourceType;
    /**
     * 资源ID(多态)。
     */
    private String resourceId;
    /**
     * 可见级别。
     */
    private String visibility;
    /**
     * 允许可见的俱乐部ID列表。
     */
    private String allowedClubIds;
    /**
     * 允许可见的用户ID列表。
     */
    private String allowedUserIds;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
