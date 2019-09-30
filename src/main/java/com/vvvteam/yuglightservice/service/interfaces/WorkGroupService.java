package com.vvvteam.yuglightservice.service.interfaces;

import com.vvvteam.yuglightservice.domain.DTO.WorkGroupInfo;
import com.vvvteam.yuglightservice.domain.DTO.registrateDTO.WorkGroupDTO;
import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.domain.entries.WorkGroup;

import java.util.List;

public interface WorkGroupService {

    List<WorkGroupInfo> getWorkGroupsInfo(Organization organization);
    void addWorkGroup(User currentUser, WorkGroupDTO registrate);
    WorkGroup getByUserFounder(User user);
    List<WorkGroup> getAllByOrganization(Organization organization);
}
