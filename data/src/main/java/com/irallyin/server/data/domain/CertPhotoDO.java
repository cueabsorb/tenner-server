package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 认证照片。
 */
@Data
@TableName("cert_photos")
public class CertPhotoDO implements Serializable {
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
     * 关联skill_certifications.id。
     */
    private String certificationId;
    /**
     * OSS URL。
     */
    private String photoUrl;
    /**
     * OSS缩略图URL。
     */
    private String thumbnailUrl;
    /**
     * 0-4排序。
     */
    private Integer displayOrder;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
