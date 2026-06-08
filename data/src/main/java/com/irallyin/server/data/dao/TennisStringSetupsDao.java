package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TennisStringSetupsDao extends AbstractStatusDao {
    public TennisStringSetupsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "tennis_string_setups", "id");
    }
}
