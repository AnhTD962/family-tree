package com.domain.backend.service;

import com.domain.backend.model.ChangeLog;
import com.domain.backend.repository.ChangeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeLogService {
    private final ChangeLogRepository logRepo;

    public void save(ChangeLog log) {
        logRepo.save(log);
    }

    public List<ChangeLog> findRecentByMember(String memberId) {
        return logRepo.findTopByMemberIdOrderByTimestampDesc(memberId);
    }
}
