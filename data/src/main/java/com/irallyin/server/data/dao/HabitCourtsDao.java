package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class HabitCourtsDao extends AbstractStatusDao {
    public HabitCourtsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "habit_courts", "id");
    }
}
