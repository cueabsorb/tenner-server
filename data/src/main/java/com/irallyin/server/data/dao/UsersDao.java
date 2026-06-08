package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UsersDao extends AbstractStatusDao {
    public UsersDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "users", "id");
    }

    public List<Map<String, Object>> findByEmail(String email) {
        return findByColumn("email", email);
    }
}
