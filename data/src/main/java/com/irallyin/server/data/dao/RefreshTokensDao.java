package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokensDao extends AbstractStatusDao {
    public RefreshTokensDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "refresh_tokens", "id");
    }
}
