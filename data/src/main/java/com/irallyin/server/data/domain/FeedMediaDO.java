package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 动态媒体。
 */
@Data
@TableName("feed_media")
public class FeedMediaDO implements Serializable {
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
     * 关联feed_posts.id。
     */
    private String postId;
    /**
     * OSS URL。
     */
    private String mediaUrl;
    /**
     * 媒体类型。
     */
    private String mediaType;
    /**
     * 0-8排序。
     */
    private Integer displayOrder;
    /**
     * OSS缩略图URL。
     */
    private String thumbnailUrl;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
}
