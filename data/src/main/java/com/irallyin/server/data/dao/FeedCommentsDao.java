package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FeedCommentsDao extends AbstractStatusDao {
    public FeedCommentsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "feed_comments", "id");
    }
}
