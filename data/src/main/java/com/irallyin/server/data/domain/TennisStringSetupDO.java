package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 穿线配置。
 */
@Data
@TableName("tennis_string_setups")
public class TennisStringSetupDO implements Serializable {
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
     * 关联rackets.id(可独立存在)。
     */
    private String racketId;
    /**
     * 竖线品牌。
     */
    private String mainStringBrand;
    /**
     * 横线品牌。
     */
    private String crossStringBrand;
    /**
     * 线型。
     */
    private String stringType;
    /**
     * 竖线磅数(35-75)。
     */
    private Double mainTensionLbs;
    /**
     * 横线磅数(35-75)。
     */
    private Double crossTensionLbs;
    /**
     * 穿线日期。
     */
    private LocalDate strungAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
