package com.irallyin.server.data.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 帖子评论。
 */
@Data
@TableName("feed_comments")
public class FeedCommentDO implements Serializable {
    /**
     * 序列化版本号。
     */
    private static final long serialVersionUID = 1L;

    /**
     * UUID v4。
     */
    private String id;
    /**
     * 关联feed_posts.id。
     */
    private String postId;
    /**
     * 评论者ID。
     */
    private String authorId;
    /**
     * 评论内容(≤300字)。
     */
    private String content;
    /**
     * 父评论ID(支持嵌套)。
     */
    private String parentCommentId;
    /**
     * 数据状态: 0=正常, -1=删除。
     */
    private Integer status;
    /**
     * 数据库字段 created_at。
     */
    private LocalDateTime createdAt;
}
