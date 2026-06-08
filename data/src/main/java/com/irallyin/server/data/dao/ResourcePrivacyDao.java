package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ResourcePrivacyDao extends AbstractStatusDao {
    public ResourcePrivacyDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "resource_privacy", "id");
    }
}
