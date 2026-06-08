package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RacketsDao extends AbstractStatusDao {
    public RacketsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "rackets", "id");
    }
}
