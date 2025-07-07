package com.domain.backend.repository;

import com.domain.backend.model.FamilyMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyMemberRepository extends MongoRepository<FamilyMember, String> {

    @Query("{'fullName': {$regex: ?0, $options: 'i'}}")
    List<FamilyMember> findByFullNameContainingIgnoreCase(String name);

    List<FamilyMember> findByFatherId(String fatherId);

    List<FamilyMember> findByMotherId(String motherId);

    List<FamilyMember> findBySpouseId(String spouseId);

    @Query("{'$or': [{'fatherId': ?0}, {'motherId': ?0}]}")
    List<FamilyMember> findChildren(String parentId);

    List<FamilyMember> findByGeneration(int generation);

    @Query("{'$or': [{'fatherId': null}, {'motherId': null}]}")
    List<FamilyMember> findRootMembers();
}
