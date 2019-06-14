package com.vvvTeam.yuglightservice.service.interfaces;

import com.vvvTeam.yuglightservice.domain.DTO.WorkGroupInfo;
import com.vvvTeam.yuglightservice.domain.DTO.registrateDTO.WorkGroupDTO;
import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.domain.entries.ControlPoint;
import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import com.vvvTeam.yuglightservice.domain.entries.WorkGroup;

import java.util.List;

public interface WorkGroupService {

    List<WorkGroupInfo> getWorkGroupsInfo(Organization organization);
    void addWorkGroup(User currentUser, WorkGroupDTO registrate);
    WorkGroup getByUserFounder(User user);
    List<WorkGroup> getAllByOrganization(Organization organization);
}
