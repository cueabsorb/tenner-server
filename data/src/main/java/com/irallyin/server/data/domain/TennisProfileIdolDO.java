package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 偶像球星。
 */
@Data
@TableName("tennis_profile_idols")
public class TennisProfileIdolDO implements Serializable {
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
     * 关联tennis_stars.id。
     */
    private String tennisStarId;
    /**
     * 自定义球星名称。
     */
    private String customName;
    /**
     * 0-4排序。
     */
    private Integer displayOrder;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
