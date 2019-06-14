package com.vvvTeam.yuglightservice.service.request.and.response.Responses;

import com.vvvTeam.yuglightservice.domain.entries.Group;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
public class ResponceGroups {
    private String msg;
    private List<Group> groups;
}
