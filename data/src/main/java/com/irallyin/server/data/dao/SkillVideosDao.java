package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SkillVideosDao extends AbstractStatusDao {
    public SkillVideosDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "skill_videos", "id");
    }
}
