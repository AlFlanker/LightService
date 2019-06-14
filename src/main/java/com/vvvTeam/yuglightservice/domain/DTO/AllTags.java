package com.vvvTeam.yuglightservice.domain.DTO;

import lombok.Data;

import java.util.List;

@Data
public class AllTags {
    private String type;
    private List<String> fields;

    public AllTags(String type, List<String> fields) {
        this.type = type;
        this.fields = fields;
    }
}
