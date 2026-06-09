package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 俱乐部。
 */
@Data
@TableName("clubs")
public class ClubDO implements Serializable {
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
     * 俱乐部名称(同城唯一)。
     */
    private String name;
    /**
     * 英文名。
     */
    private String englishName;
    /**
     * 城市编码。
     */
    private String cityCode;
    /**
     * 描述。
     */
    private String description;
    /**
     * OSS封面图URL。
     */
    private String coverImageUrl;
    /**
     * 创建者ID。
     */
    private String ownerId;
    /**
     * 加入策略。
     */
    private String joinPolicy;
    /**
     * 成员数(异步回写)。
     */
    private Integer memberCount;
    /**
     * 俱乐部状态。
     */
    private String clubStatus;
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
