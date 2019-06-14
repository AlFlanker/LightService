package com.vvvTeam.yuglightservice.service.request.and.response.report;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor

public class ReportBody {
    private Long id;
    private List<Object[]> states;

    public ReportBody(Long id, List<Object[]> states) {
        this.id = id;
        this.states = states;
    }
}
