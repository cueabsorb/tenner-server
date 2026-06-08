package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlayParticipantsDao extends AbstractStatusDao {
    public PlayParticipantsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "play_participants", "id");
    }
}
