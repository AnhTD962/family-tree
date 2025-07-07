package com.domain.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("change_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeLog {
    @Id
    private String id;
    private String memberId;
    private String action; // CREATE, UPDATE, DELETE
    private String oldData;
    private String newData;
    private String changedBy;
    private LocalDateTime timestamp;
}
