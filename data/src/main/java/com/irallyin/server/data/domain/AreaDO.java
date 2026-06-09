package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 区域。
 */
@Data
@TableName("areas")
public class AreaDO implements Serializable {
    /**
     * 序列化版本号。
     */
    private static final long serialVersionUID = 1L;

    /**
     * 区域编码。
     */
    @TableId
    private String code;
    /**
     * 关联cities.code。
     */
    private String cityCode;
    /**
     * 区域名。
     */
    private String name;
    /**
     * 英文名。
     */
    private String englishName;
    /**
     * 纬度。
     */
    private Double latitude;
    /**
     * 经度。
     */
    private Double longitude;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
