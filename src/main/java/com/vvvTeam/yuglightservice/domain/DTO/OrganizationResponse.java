package com.vvvTeam.yuglightservice.domain.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class OrganizationResponse {
    private String status;
    private List<OrganizationInfo> infoList;
}
