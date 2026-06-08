package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FeedMediaDao extends AbstractStatusDao {
    public FeedMediaDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "feed_media", "id");
    }
}
