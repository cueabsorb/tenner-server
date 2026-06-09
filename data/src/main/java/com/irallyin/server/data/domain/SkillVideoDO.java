package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 认证视频。
 */
@Data
@TableName("skill_videos")
public class SkillVideoDO implements Serializable {
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
     * 关联skill_certifications.id (唯一)。
     */
    private String certificationId;
    /**
     * OSS URL。
     */
    private String videoUrl;
    /**
     * OSS缩略图。
     */
    private String thumbnailUrl;
    /**
     * 时长秒数(≤60)。
     */
    private Integer durationSeconds;
    /**
     * 文件大小MB。
     */
    private Double fileSizeMb;
    /**
     * 数据库字段 created_at。
     */
    private LocalDateTime createdAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
