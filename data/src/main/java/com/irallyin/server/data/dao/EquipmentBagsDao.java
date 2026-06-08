package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class EquipmentBagsDao extends AbstractStatusDao {
    public EquipmentBagsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "equipment_bags", "id");
    }
}
