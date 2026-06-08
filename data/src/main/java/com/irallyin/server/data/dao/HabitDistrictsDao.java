package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class HabitDistrictsDao extends AbstractStatusDao {
    public HabitDistrictsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "habit_districts", "id");
    }
}
