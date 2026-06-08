package com.irallyin.server.data.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class UserEntity {
    @TableId
    private String id;
    private String phone;
    private String email;
    private String passwordHash;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private String gender;
    private java.time.LocalDate birthday;
    private String country;
    private String province;
    private String city;
    private String district;
    private Boolean realNameVisible;
    private String locale;
    private String timezone;
    private Boolean onboardingCompleted;
    private Integer onboardingStep;
    private String playerIdentity;
    private Double ntrpRating;
    private Double sysNtrpRating;
    private String dominantHand;
    private Integer accessStatus;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
