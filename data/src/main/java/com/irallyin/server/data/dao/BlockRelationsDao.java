package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BlockRelationsDao extends AbstractStatusDao {
    public BlockRelationsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "block_relations", "id");
    }
}
