package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TennisProfileIdolsDao extends AbstractStatusDao {
    public TennisProfileIdolsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "tennis_profile_idols", "id");
    }
}
