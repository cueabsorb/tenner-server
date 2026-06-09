package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 发球类型。
 */
@Data
@TableName("tennis_profile_serve_types")
public class TennisProfileServeTypeDO implements Serializable {
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
     * 关联tennis_profiles.id。
     */
    private String profileId;
    /**
     * 发球类型。
     */
    private String serveType;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
