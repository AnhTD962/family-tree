package com.domain.backend.service;

import com.domain.backend.model.ChangeLog;
import com.domain.backend.model.FamilyMember;
import com.domain.backend.repository.FamilyMemberRepository;
import com.domain.backend.utils.TreeBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyMemberServiceImpl implements FamilyMemberService {

    private final FamilyMemberRepository memberRepo;
    private final FileStorageService fileStorageService;
    private final ChangeLogService changeLogService;
    private final TreeBuilder treeBuilder;
    private final ExportService exportService;
    private final UndoService undoService;
    private final ObjectMapper mapper;

    @Override
    public List<FamilyMember> getAll() {
        return memberRepo.findAll();
    }

    @Override
    public FamilyMember getById(String id) {
        return memberRepo.findById(id).orElseThrow();
    }

    @Override
    public FamilyMember create(FamilyMember member) {
        FamilyMember saved = memberRepo.save(member);
        saveLog("CREATE", null, saved);
        return saved;
    }

    @Override
    public FamilyMember update(String id, FamilyMember member) {
        FamilyMember existing = memberRepo.findById(id).orElseThrow();
        saveLog("UPDATE", existing, member);
        member.setId(id);
        return memberRepo.save(member);
    }

    @Override
    public void delete(String id) {
        FamilyMember member = memberRepo.findById(id).orElseThrow();
        saveLog("DELETE", member, null);

        // Gỡ liên kết khỏi các thành viên khác
        for (String relId : member.getParents()) {
            memberRepo.findById(relId).ifPresent(p -> {
                p.getChildren().remove(id);
                memberRepo.save(p);
            });
        }
        for (String relId : member.getChildren()) {
            memberRepo.findById(relId).ifPresent(c -> {
                c.getParents().remove(id);
                memberRepo.save(c);
            });
        }
        for (String relId : member.getSpouses()) {
            memberRepo.findById(relId).ifPresent(s -> {
                s.getSpouses().remove(id);
                memberRepo.save(s);
            });
        }
        memberRepo.deleteById(id);
    }

    @Override
    public FamilyMember addRelation(String id, String targetId, String type) {
        FamilyMember source = memberRepo.findById(id).orElseThrow();
        FamilyMember target = memberRepo.findById(targetId).orElseThrow();

        switch (type.toLowerCase()) {
            case "parent" -> {
                source.getParents().add(targetId);
                target.getChildren().add(id);
            }
            case "child" -> {
                source.getChildren().add(targetId);
                target.getParents().add(id);
            }
            case "spouse" -> {
                source.getSpouses().add(targetId);
                target.getSpouses().add(id);
            }
        }
        memberRepo.save(target);
        return memberRepo.save(source);
    }

    @Override
    public List<FamilyMember> search(String name, String gender, String birthYear) {
        return memberRepo.search(name, gender, birthYear);
    }

    @Override
    public String uploadAvatar(String id, MultipartFile file) {
        FamilyMember member = memberRepo.findById(id).orElseThrow();
        String url = fileStorageService.storeFile(id, file);
        member.setAvatarUrl(url);
        memberRepo.save(member);
        return url;
    }

    @Override
    public Object generateTreeFromRoot(String rootId) {
        return treeBuilder.buildTree(rootId);
    }

    @Override
    public String exportTreeAsJson(String rootId) {
        return exportService.exportTreeAsJson(rootId);
    }

    @Override
    public void undoLastChange(String memberId) {
        undoService.undo(memberId);
    }

    private void saveLog(String action, FamilyMember oldData, FamilyMember newData) {
        try {
            changeLogService.save(ChangeLog.builder()
                    .action(action)
                    .timestamp(LocalDateTime.now())
                    .memberId(newData != null ? newData.getId() : oldData.getId())
                    .oldData(oldData != null ? mapper.writeValueAsString(oldData) : null)
                    .newData(newData != null ? mapper.writeValueAsString(newData) : null)
                    .changedBy("system") // có thể thay bằng user từ token
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Cannot save log", e);
        }
    }
}
