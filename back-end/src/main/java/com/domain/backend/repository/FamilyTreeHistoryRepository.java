package com.domain.backend.repository;

import com.domain.backend.model.FamilyTreeHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyTreeHistoryRepository extends MongoRepository<FamilyTreeHistory, String> {
    List<FamilyTreeHistory> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);

    List<FamilyTreeHistory> findAllByOrderByTimestampDesc(Pageable pageable);
}
