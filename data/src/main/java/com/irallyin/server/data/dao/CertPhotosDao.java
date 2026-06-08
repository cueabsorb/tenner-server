package com.irallyin.server.data.dao;

import com.irallyin.server.data.mapper.GenericStatusMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CertPhotosDao extends AbstractStatusDao {
    public CertPhotosDao(GenericStatusMapper genericStatusMapper) {
        super(genericStatusMapper, "cert_photos", "id");
    }
}
