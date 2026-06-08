package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class LoginAuditLogDao extends AbstractStatusDao {
    public LoginAuditLogDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "login_audit_log", "id");
    }
}
