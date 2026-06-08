package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TennisProfileStrengthTagsDao extends AbstractStatusDao {
    public TennisProfileStrengthTagsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "tennis_profile_strength_tags", "id");
    }
}
