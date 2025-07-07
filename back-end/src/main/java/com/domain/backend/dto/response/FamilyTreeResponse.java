package com.domain.backend.dto.response;

import com.domain.backend.dto.FamilyMemberDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyTreeResponse {
    private List<FamilyMemberDTO> members;
}
