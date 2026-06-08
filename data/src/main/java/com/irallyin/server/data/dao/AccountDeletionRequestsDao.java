package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDeletionRequestsDao extends AbstractStatusDao {
    public AccountDeletionRequestsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "account_deletion_requests", "id");
    }
}
