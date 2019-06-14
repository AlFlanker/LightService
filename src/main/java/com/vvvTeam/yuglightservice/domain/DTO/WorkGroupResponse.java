package com.vvvTeam.yuglightservice.domain.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class WorkGroupResponse {
    private String status;
    private List<WorkGroupInfo> groupInfoList;
}
