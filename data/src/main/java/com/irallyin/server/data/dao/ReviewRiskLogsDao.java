package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewRiskLogsDao extends AbstractStatusDao {
    public ReviewRiskLogsDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "review_risk_logs", "id");
    }
}
