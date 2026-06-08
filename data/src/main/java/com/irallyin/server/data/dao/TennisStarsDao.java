package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TennisStarsDao extends AbstractStatusDao {
    public TennisStarsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "tennis_stars", "id");
    }
}
