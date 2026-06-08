package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleOverridesDao extends AbstractStatusDao {
    public ScheduleOverridesDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "schedule_overrides", "id");
    }
}
