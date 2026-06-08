package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ClubMembersDao extends AbstractStatusDao {
    public ClubMembersDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "club_members", "id");
    }
}
