package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CourtsDao extends AbstractStatusDao {
    public CourtsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "courts", "id");
    }
}
