package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TennisProfileServeTypesDao extends AbstractStatusDao {
    public TennisProfileServeTypesDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "tennis_profile_serve_types", "id");
    }
}
