package com.domain.backend.repository;

import com.domain.backend.model.FamilyTreeHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyTreeHistoryRepository extends MongoRepository<FamilyTreeHistory, String> {
}
