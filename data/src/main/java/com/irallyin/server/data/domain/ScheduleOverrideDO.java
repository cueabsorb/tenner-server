package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 日程覆盖。
 */
@Data
@TableName("schedule_overrides")
public class ScheduleOverrideDO implements Serializable {
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
     * 具体日期。
     */
    private LocalDate date;
    /**
     * 是否可打球。
     */
    private Boolean isAvailable;
    /**
     * 备注如"出差"。
     */
    private String note;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
