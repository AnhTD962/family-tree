package com.domain.backend.service;

import com.domain.backend.model.FamilyMember;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FamilyMemberService {
    List<FamilyMember> getAll();
    FamilyMember getById(String id);
    FamilyMember create(FamilyMember member);
    FamilyMember update(String id, FamilyMember member);
    void delete(String id);
    FamilyMember addRelation(String id, String targetId, String type);
    List<FamilyMember> search(String name, String gender, String birthYear);
    String uploadAvatar(String id, MultipartFile file);
    Object generateTreeFromRoot(String rootId);
    String exportTreeAsJson(String rootId);
    void undoLastChange(String memberId);
}
