package com.domain.backend.repository;

import com.domain.backend.model.FamilyMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FamilyMemberRepository extends MongoRepository<FamilyMember, String> {
    @Query("{ 'fullName': { $regex: ?0, $options: 'i' }, 'gender': { $regex: ?1 }, 'dateOfBirth': { $regex: ?2 } }")
    List<FamilyMember> search(String name, String gender, String birthYear);
}
