package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ClubsDao extends AbstractStatusDao {
    public ClubsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "clubs", "id");
    }
}
