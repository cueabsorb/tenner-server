package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 动态帖子。
 */
@Data
@TableName("feed_posts")
public class FeedPostDO implements Serializable {
    /**
     * 序列化版本号。
     */
    private static final long serialVersionUID = 1L;

    /**
     * UUID v4。
     */
    private String id;
    /**
     * 作者ID。
     */
    private String authorId;
    /**
     * 帖子文字(≤500字)。
     */
    private String text;
    /**
     * 图片URL数组(1-9张)。
     */
    private String imageUrls;
    /**
     * 关联运动记录ID。
     */
    private String playSessionId;
    /**
     * 关联球场ID。
     */
    private String courtId;
    /**
     * 话题标签数组。
     */
    private String topicTags;
    /**
     * 可见范围。
     */
    private String visibility;
    /**
     * 点赞数(异步回写)。
     */
    private Integer likeCount;
    /**
     * 评论数(异步回写)。
     */
    private Integer commentCount;
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
}
