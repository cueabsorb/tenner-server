package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ClubJoinRequestsDao extends AbstractStatusDao {
    public ClubJoinRequestsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "club_join_requests", "id");
    }
}
