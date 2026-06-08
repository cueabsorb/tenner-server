package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PrivacySettingsDao extends AbstractStatusDao {
    public PrivacySettingsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "privacy_settings", "id");
    }
}
