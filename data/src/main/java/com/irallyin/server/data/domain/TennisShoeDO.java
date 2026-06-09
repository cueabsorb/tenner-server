package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 球鞋。
 */
@Data
@TableName("tennis_shoes")
public class TennisShoeDO implements Serializable {
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
     * 关联equipment_bags.id。
     */
    private String bagId;
    /**
     * 品牌。
     */
    private String brand;
    /**
     * 型号。
     */
    private String model;
    /**
     * 尺码。
     */
    private String size;
    /**
     * 适用场地。
     */
    private String courtType;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
