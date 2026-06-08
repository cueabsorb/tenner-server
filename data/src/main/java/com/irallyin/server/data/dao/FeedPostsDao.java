package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FeedPostsDao extends AbstractStatusDao {
    public FeedPostsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "feed_posts", "id");
    }
}
