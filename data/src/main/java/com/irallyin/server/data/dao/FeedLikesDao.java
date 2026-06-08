package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FeedLikesDao extends AbstractStatusDao {
    public FeedLikesDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "feed_likes", "id");
    }
}
