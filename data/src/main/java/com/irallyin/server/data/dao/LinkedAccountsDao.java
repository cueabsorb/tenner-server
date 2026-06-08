package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class LinkedAccountsDao extends AbstractStatusDao {
    public LinkedAccountsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "linked_accounts", "id");
    }
}
