package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerReviewsDao extends AbstractStatusDao {
    public PlayerReviewsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "player_reviews", "id");
    }
}
