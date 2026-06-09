package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 习惯关联球场。
 */
@Data
@TableName("habit_courts")
public class HabitCourtDO implements Serializable {
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
     * 关联courts.id。
     */
    private String courtId;
    /**
     * 是否首选球场(仅一个)。
     */
    private Boolean isPrimary;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
