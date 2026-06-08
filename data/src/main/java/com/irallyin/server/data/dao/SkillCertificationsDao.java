package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SkillCertificationsDao extends AbstractStatusDao {
    public SkillCertificationsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "skill_certifications", "id");
    }
}
