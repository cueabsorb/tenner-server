package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 城市。
 */
@Data
@TableName("cities")
public class CityDO implements Serializable {
    /**
     * 序列化版本号。
     */
    private static final long serialVersionUID = 1L;

    /**
     * 城市编码如SHA。
     */
    @TableId
    private String code;
    /**
     * 城市名。
     */
    private String name;
    /**
     * 英文名。
     */
    private String englishName;
    /**
     * ISO 3166-1 alpha-2。
     */
    private String countryCode;
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
