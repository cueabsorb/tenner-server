package com.irallyin.server.data.dao;

import com.irallyin.server.data.domain.UserDO;
import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsersDao extends AbstractStatusDao {
    public UsersDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "users", "id");
    }

    public List<UserDO> findByEmail(String email) {
        return findByColumn("email", email, UserDO.class);
    }
}
