package com.vvvTeam.yuglightservice.service.request.and.response.Responses;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class WorkGroup4Map {
    private long id;
    private String name;
    private Set<Groups4Map> groups;
}
