package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TennisShoesDao extends AbstractStatusDao {
    public TennisShoesDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "tennis_shoes", "id");
    }
}
