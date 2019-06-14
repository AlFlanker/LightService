package com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request;

import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.auth.Role;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.service.interfaces.OrganizationService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Scope("prototype")
@Component
@NoArgsConstructor
public class MapGetAllOrg extends MapRequest implements MapExecutable {
    @Autowired
    private OrganizationService organizationService;

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    @Override
    public MapResponse execute(User user, MapType objectType) {
        MapResponseGetAll response = new MapResponseGetAll<Organization4Map>();
        if(user.getRoles().contains(Role.ADMIN)){
            response.setType(MapObjectType.ALL_ORGANIZATION);
            response.setStatus(MapStatus.success);
            List<Organization4Map> organization4Map = new ArrayList<Organization4Map>();
            List<Organization> organizations = organizationService.getAll();
            for (Organization org : organizations){
                Organization4Map org4Map = new Organization4Map();
                org4Map.setId(org.getId());
                org4Map.setName(org.getName());
                org4Map.setWorkGroup4MapList(org.getWorkGroups().stream().filter(group -> !group.isDeleted()).map(workGroup -> {
                    WorkGroup4Map workGroup4Map = new WorkGroup4Map();
                    workGroup4Map.setId(workGroup.getId());
                    workGroup4Map.setName(workGroup.getName());
                    workGroup4Map.setGroups(workGroup.getWgGroups().stream().filter(group -> !group.isDeleted()).map(group -> {
                        Groups4Map groups4Map = new Groups4Map();
                        groups4Map.setId(group.getId());
                        groups4Map.setName(group.getNameOfGroup());
                        return groups4Map;
                    }).collect(Collectors.toSet()));
                    return workGroup4Map;
                }).collect(Collectors.toSet()));
                organization4Map.add(org4Map);
            }
            response.setObject(organization4Map);
            return response;
        }
        response.setStatus(MapStatus.bad_data);
        return response;
    }
}
