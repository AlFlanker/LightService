package com.vvvteam.yuglightservice.domain.DTO.report;

import lombok.Data;

import java.util.List;
@Data
public class AllObj {
    private String type;
    private List<Object[]> fields;
}
