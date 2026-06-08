package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CitiesDao extends AbstractStatusDao {
    public CitiesDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "cities", "code");
    }
}
