package com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request;

import com.vvvTeam.yuglightservice.domain.auth.Role;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.domain.entries.WorkGroup;
import com.vvvTeam.yuglightservice.service.interfaces.WorkGroupService;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.MapExecutable;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapRequest;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapType;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.MapResponseGetAll;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapObjectType;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapStatus;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.Groups4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.Organization4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.WorkGroup4Map;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Scope("prototype")
@Component
@NoArgsConstructor
public class MapGetAllWG extends MapRequest implements MapExecutable {
    @Autowired
    private WorkGroupService workGroupService;

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    @Override
    public MapResponse execute(User user, MapType objectType) {
        MapResponseGetAll response = new MapResponseGetAll<Organization4Map>();
        if(user.getRoles().contains(Role.SuperUserOwner)) {
            List<WorkGroup> workGroups = workGroupService.getAllByOrganization(user.getOrganizationOwner());
            Organization4Map org4Map = new Organization4Map();
            org4Map.setId(user.getOrganizationOwner().getId());
            org4Map.setName(user.getOrganizationOwner().getName());
            response.setType(MapObjectType.ALL_WORK_GROUP);
            Set<WorkGroup4Map> resp = workGroups.stream().map(workGroup -> {
                WorkGroup4Map workGroup4Map = new WorkGroup4Map();
                workGroup4Map.setId(workGroup.getId());
                workGroup4Map.setName(workGroup.getName());
                workGroup4Map.setGroups(workGroup.getWgGroups().stream().map(group -> {
                    Groups4Map groups4Map = new Groups4Map();
                    groups4Map.setId(group.getId());
                    groups4Map.setName(group.getNameOfGroup());
                    return groups4Map;
                }).collect(Collectors.toSet()));
                return workGroup4Map;
            }).collect(Collectors.toSet());
            org4Map.setWorkGroup4MapList(resp);
            response.setObject(Collections.singletonList(org4Map));
            response.setStatus(MapStatus.success);
            return response;
        }
        response.setStatus(MapStatus.bad_data);
        return response;
    }

}
