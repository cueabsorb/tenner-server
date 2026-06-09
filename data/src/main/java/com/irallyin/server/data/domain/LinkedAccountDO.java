package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 第三方账号绑定。
 */
@Data
@TableName("linked_accounts")
public class LinkedAccountDO implements Serializable {
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
     * 关联用户ID。
     */
    private String userId;
    /**
     * 认证提供商。
     */
    private String provider;
    /**
     * 第三方平台用户ID。
     */
    private String providerUserId;
    /**
     * Apple隐藏邮箱等。
     */
    private String providerEmail;
    /**
     * 第三方昵称。
     */
    private String providerNickname;
    /**
     * 第三方头像。
     */
    private String providerAvatarUrl;
    /**
     * 绑定时间。
     */
    private LocalDateTime linkedAt;
    /**
     * 最后登录时间。
     */
    private LocalDateTime lastLoginAt;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
    /**
     * 第三方邮箱是否已验证。
     */
    private Boolean providerEmailVerified;
    /**
     * 第三方账号语言区域。
     */
    private String providerLocale;
    /**
     * 最近一次授权scope。
     */
    private String providerScope;
    /**
     * 第三方资料最近更新时间。
     */
    private LocalDateTime providerLinkUpdatedAt;
}
