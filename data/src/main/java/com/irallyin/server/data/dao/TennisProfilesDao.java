package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TennisProfilesDao extends AbstractStatusDao {
    public TennisProfilesDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "tennis_profiles", "id");
    }
}
