package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class HabitCitiesDao extends AbstractStatusDao {
    public HabitCitiesDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "habit_cities", "id");
    }
}
