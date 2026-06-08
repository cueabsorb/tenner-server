package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlaySessionsDao extends AbstractStatusDao {
    public PlaySessionsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "play_sessions", "id");
    }
}
