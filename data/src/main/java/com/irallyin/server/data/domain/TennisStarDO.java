package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 网球明星。
 */
@Data
@TableName("tennis_stars")
public class TennisStarDO implements Serializable {
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
     * 姓名。
     */
    private String name;
    /**
     * ISO 3166-1国籍。
     */
    private String country;
    /**
     * OSS头像URL。
     */
    private String avatarUrl;
    /**
     * 活跃状态。
     */
    private String activeStatus;
    /**
     * 持拍手。
     */
    private String handedness;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
