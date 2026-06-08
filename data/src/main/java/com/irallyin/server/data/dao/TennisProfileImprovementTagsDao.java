package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TennisProfileImprovementTagsDao extends AbstractStatusDao {
    public TennisProfileImprovementTagsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "tennis_profile_improvement_tags", "id");
    }
}
