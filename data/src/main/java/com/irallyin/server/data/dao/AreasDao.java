package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AreasDao extends AbstractStatusDao {
    public AreasDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "areas", "code");
    }
}
