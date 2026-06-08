package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class HabitWeeklySlotsDao extends AbstractStatusDao {
    public HabitWeeklySlotsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "habit_weekly_slots", "id");
    }
}
