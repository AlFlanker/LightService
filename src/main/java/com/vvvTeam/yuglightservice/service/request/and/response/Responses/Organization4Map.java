package com.vvvTeam.yuglightservice.service.request.and.response.Responses;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Data
@Getter @Setter
@NoArgsConstructor
public class Organization4Map {
    private long id;
    private String name;
    private Set<WorkGroup4Map> workGroup4MapList;

    public Organization4Map(long id, String name, Set<WorkGroup4Map> workGroup4MapList) {
        this.id = id;
        this.name = name;
        this.workGroup4MapList = workGroup4MapList;
    }

    public Organization4Map(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
