package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 球拍。
 */
@Data
@TableName("rackets")
public class RacketDO implements Serializable {
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
     * 握把尺寸如4_3/8。
     */
    private String gripSize;
    /**
     * 重量(克)。
     */
    private Integer weightGram;
    /**
     * 拍面大小(平方英寸)。
     */
    private Integer headSizeSqIn;
    /**
     * OSS URL。
     */
    private String imageUrl;
    /**
     * 是否主用球拍。
     */
    private Boolean isPrimary;
    /**
     * 排序。
     */
    private Integer displayOrder;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
