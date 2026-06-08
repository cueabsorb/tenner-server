package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerSkillProfilesDao extends AbstractStatusDao {
    public PlayerSkillProfilesDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "player_skill_profiles", "id");
    }
}
