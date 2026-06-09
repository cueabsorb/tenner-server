package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 隐私设置。
 */
@Data
@TableName("privacy_settings")
public class PrivacySettingDO implements Serializable {
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
     * 关联ir_auth.users.id。
     */
    private String userId;
    /**
     * 隐私类别。
     */
    private String category;
    /**
     * 可见级别。
     */
    private String visibility;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
