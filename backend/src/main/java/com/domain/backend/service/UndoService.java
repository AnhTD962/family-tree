package com.domain.backend.service;

import com.domain.backend.model.ChangeLog;
import com.domain.backend.model.FamilyMember;
import com.domain.backend.repository.ChangeLogRepository;
import com.domain.backend.repository.FamilyMemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UndoService {
    private final ChangeLogRepository logRepo;
    private final FamilyMemberRepository memberRepo;
    private final ObjectMapper mapper;

    public void undo(String memberId) {
        ChangeLog log = logRepo.findTopByMemberIdOrderByTimestampDesc(memberId).stream().findFirst().orElse(null);
        if (log != null && log.getOldData() != null) {
            try {
                FamilyMember previous = mapper.readValue(log.getOldData(), FamilyMember.class);
                memberRepo.save(previous);
            } catch (Exception e) {
                throw new RuntimeException("Failed to undo", e);
            }
        }
    }
}
