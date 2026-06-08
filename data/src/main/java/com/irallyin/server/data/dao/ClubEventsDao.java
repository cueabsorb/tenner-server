package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ClubEventsDao extends AbstractStatusDao {
    public ClubEventsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "club_events", "id");
    }
}
