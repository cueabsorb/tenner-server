package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlayingHabitsDao extends AbstractStatusDao {
    public PlayingHabitsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "playing_habits", "id");
    }
}
