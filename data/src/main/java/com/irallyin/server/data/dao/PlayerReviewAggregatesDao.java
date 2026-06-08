package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerReviewAggregatesDao extends AbstractStatusDao {
    public PlayerReviewAggregatesDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "player_review_aggregates", "id");
    }
}
