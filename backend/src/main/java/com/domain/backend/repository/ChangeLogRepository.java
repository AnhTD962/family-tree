package com.domain.backend.repository;

import com.domain.backend.model.ChangeLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChangeLogRepository extends MongoRepository<ChangeLog, String> {
    List<ChangeLog> findTopByMemberIdOrderByTimestampDesc(String memberId);
}
