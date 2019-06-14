package com.vvvTeam.yuglightservice.domain.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class WorkGroupBaseObjResponse extends WorkGroupObjResponse{
    private String status;
    private long totalElem;
    private int totalPage;
    private int currentPage;
    private List<BaseObjDTO> data;

}
