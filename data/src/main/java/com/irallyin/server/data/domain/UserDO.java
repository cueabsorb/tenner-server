package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户主表。
 */
@Data
@TableName("users")
public class UserDO implements Serializable {
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
     * E.164格式 如+86-13800138000。
     */
    private String phone;
    /**
     * RFC 5322。
     */
    private String email;
    /**
     * Argon2id hash, 仅邮箱注册。
     */
    private String passwordHash;
    /**
     * 2-20字符。
     */
    private String displayName;
    /**
     * OSS URL。
     */
    private String avatarUrl;
    /**
     * 国家/地区。
     */
    private String country;
    /**
     * 城市。
     */
    private String city;
    /**
     * IETF语言标签。
     */
    private String locale;
    /**
     * IANA时区。
     */
    private String timezone;
    /**
     * 是否完成新手引导。
     */
    private Boolean onboardingCompleted;
    /**
     * 0=未开始 1-5=各步骤。
     */
    private Integer onboardingStep;
    /**
     * 球员身份。
     */
    private String playerIdentity;
    /**
     * 用户自评NTRP等级 1.0-5.5。
     */
    private Double ntrpRating;
    /**
     * 系统根据历史数据计算的NTRP等级。
     */
    private Double sysNtrpRating;
    /**
     * 用户访问状态: 0=正常可用, 1=禁止访问, 2=部分功能禁止(不能搜索/查看/上传等)。
     */
    private Integer accessStatus;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
    /**
     * 数据库字段 created_at。
     */
    private LocalDateTime createdAt;
    /**
     * 数据库字段 updated_at。
     */
    private LocalDateTime updatedAt;
    /**
     * 软删除时间。
     */
    private LocalDateTime deletedAt;
    /**
     * 个人主页简介。
     */
    private String bio;
    /**
     * 性别。
     */
    private String gender;
    /**
     * 生日。
     */
    private LocalDate birthday;
    /**
     * 省/州。
     */
    private String province;
    /**
     * 区/县。
     */
    private String district;
    /**
     * 是否展示实名信息。
     */
    private Boolean realNameVisible;
    /**
     * 常用手。
     */
    private String dominantHand;
}
