package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserReportsDao extends AbstractStatusDao {
    public UserReportsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "user_reports", "id");
    }
}
