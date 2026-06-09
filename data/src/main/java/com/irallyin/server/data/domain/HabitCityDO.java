package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 习惯关联城市。
 */
@Data
@TableName("habit_cities")
public class HabitCityDO implements Serializable {
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
     * 关联playing_habits.id。
     */
    private String habitId;
    /**
     * 关联cities.code。
     */
    private String cityCode;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
