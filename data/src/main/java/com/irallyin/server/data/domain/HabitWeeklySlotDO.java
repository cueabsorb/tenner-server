package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 每周打球时间段。
 */
@Data
@TableName("habit_weekly_slots")
public class HabitWeeklySlotDO implements Serializable {
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
     * 1=周一 7=周日。
     */
    private Integer dayOfWeek;
    /**
     * 开始时间(本地)。
     */
    private String startTime;
    /**
     * 结束时间(本地)。
     */
    private String endTime;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
