package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerRelationshipEdgesDao extends AbstractStatusDao {
    public PlayerRelationshipEdgesDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "player_relationship_edges", "id");
    }
}
