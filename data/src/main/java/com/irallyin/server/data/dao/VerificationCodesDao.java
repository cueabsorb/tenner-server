package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class VerificationCodesDao extends AbstractStatusDao {
    public VerificationCodesDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "verification_codes", "id");
    }
}
