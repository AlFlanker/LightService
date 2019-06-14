package com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.auth.Role;
import com.vvvTeam.yuglightservice.domain.entries.WorkGroup;
import com.vvvTeam.yuglightservice.repositories.OrganizationRepo;
import com.vvvTeam.yuglightservice.service.interfaces.ControlPointService;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.MapExecutable;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapRequest;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapType;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.MapResponseGetAll;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapObjectType;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapStatus;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.BaseObj4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.Organization4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.ResponceControlPoint;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.ResponceControlPoints;
import com.vvvTeam.yuglightservice.domain.entries.ControlPoint;
import com.vvvTeam.yuglightservice.domain.entries.Group;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import com.vvvTeam.yuglightservice.repositories.ControlPointsRepo;
import com.vvvTeam.yuglightservice.repositories.GroupRepository;
import com.vvvTeam.yuglightservice.service.interfaces.LampService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Init bean через сетеры из-за необходимости конструктора без параметров для Jackson!
 */
@Slf4j
@Scope("prototype")
@Component
@NoArgsConstructor
public class MapGetAll extends MapRequest implements MapExecutable {
    @JsonIgnore
    private OrganizationRepo organizationRepo;

    @JsonIgnore
    private LampService objectsService;

    @JsonIgnore
    private ControlPointService controlPointService;

    @JsonIgnore
    private  ControlPointsRepo controlPointsRepo;

    @JsonIgnore
    private  GroupRepository groupRepository;

    @Autowired
    public void setControlPointService(ControlPointService controlPointService) {
        this.controlPointService = controlPointService;
    }

    @Autowired
    public void setObjectsService(LampService objectsService) {
        this.objectsService = objectsService;
    }
    @Autowired
    public void setControlPointsRepo(ControlPointsRepo controlPointsRepo) {
        this.controlPointsRepo = controlPointsRepo;
    }
    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }
    @Autowired
    public void setOrganizationRepo(OrganizationRepo organizationRepo) {
        this.organizationRepo = organizationRepo;
    }

    /**
     * Подумать о Pageable. Если данных много - разбить на этапы!
     * @param user
     * @param type
     * @return MapResponse
     */
    @Override
    @Transactional(readOnly = true)
    public MapResponse execute(User user, MapType type) {

        switch (type) {
            case Lamp: {
                final List<Lamp> lamps = new ArrayList<>();
                MapResponseGetAll response = new MapResponseGetAll<BaseObj4Map>();
                if(user.getRoles().contains(Role.ADMIN)){
                    List<Organization> organizations = organizationRepo.findAll();
                    Set<WorkGroup> allWorkGroup = new HashSet<WorkGroup>();
                    organizations.forEach(o->allWorkGroup.addAll(o.getWorkGroups()));
                    allWorkGroup.forEach((workGroup->{
                        lamps.addAll(Optional.of(objectsService.getAllLampByWorkGroup(workGroup))
                                .orElse(Collections.emptyList()));}));

                }
                else if(user.getRoles().contains(Role.SuperUserOwner)){
                    Set<WorkGroup> workGroups = user.getOrganizationOwner().getWorkGroups();

                    workGroups.forEach(workGroup->{
                        lamps.addAll(Optional.of(objectsService.getAllLampByWorkGroup(workGroup))
                                .orElse(Collections.emptyList()));

                    });


                }
                else {
                    if (user.getWorkGroup() != null) {
                        lamps.addAll(Optional.of(objectsService.getAllLampByWorkGroup(user.getWorkGroup()))
                                .orElse(Collections.emptyList()));

                    }
                }
                response.setType(MapObjectType.allLamps);
                response.setStatus(MapStatus.success);
                response.setObject(objectsService.getObj4MapWithLastStates(lamps));
                return response;
            }
            case CP: {
                final List<ControlPoint> cps = new ArrayList<>();
                MapResponseGetAll response = new MapResponseGetAll<ResponceControlPoint>();
                if(user.getRoles().contains(Role.ADMIN)){
                    List<Organization> organizations = organizationRepo.findAll();
                    Set<WorkGroup> allWorkGroup = new HashSet<WorkGroup>();
                    organizations.forEach(o->allWorkGroup.addAll(o.getWorkGroups()));
                    allWorkGroup.forEach((workGroup->{
                        cps.addAll(Optional.of(controlPointsRepo.findByWorkGroupAndIsDeleted(workGroup,false))
                                .orElse(Collections.emptyList()));}));

                }
                else if(user.getRoles().contains(Role.SuperUserOwner)){
                    Set<WorkGroup> workGroups = user.getOrganizationOwner().getWorkGroups();

                    workGroups.forEach(workGroup->{
                        cps.addAll(Optional.of(controlPointsRepo.findByWorkGroupAndIsDeleted(workGroup,false))
                                .orElse(Collections.emptyList()));
                    });
                }
                else {
                    if (user.getWorkGroup() != null) {
                        cps.addAll(Optional.of(controlPointsRepo.findByWorkGroup(user.getWorkGroup(), PageRequest.of(0, 1000)))
                                .orElse(Page.empty()).getContent());

                    }
                }
                response.setStatus(MapStatus.success);
                response.setType(MapObjectType.allCP);
                response.setObject(controlPointService.getCP4Map(cps));
                return response;
            }
            case Group:{
                Map<Long,String> res = new HashMap<>();
                MapResponseGetAll response = new MapResponseGetAll<Map.Entry<Long,String>>();
                if(user.getRoles().contains(Role.ADMIN)){
                    List<Organization> organizations = organizationRepo.findAll();
                    Set<WorkGroup> allWorkGroup = new HashSet<WorkGroup>();
                    List<Group> groups = new ArrayList<Group>();
                    organizations.forEach(o->allWorkGroup.addAll(o.getWorkGroups()));
                   for(WorkGroup wg:allWorkGroup){
                       groups.addAll(Optional.of(groupRepository.findByWorkGroup(wg)).orElse(Collections.emptyList()));
                   }
                   res.putAll(groups.stream().collect(Collectors.toMap(Group::getId,Group::getNameOfGroup)));
                }
                else if(user.getRoles().contains(Role.SuperUserOwner)){
                    Set<WorkGroup> workGroups = user.getOrganizationOwner().getWorkGroups();
                    List<Group> groups = new ArrayList<Group>();
                    for(WorkGroup wg:workGroups){
                        groups.addAll(Optional.of(groupRepository.findByWorkGroup(wg)).orElse(Collections.emptyList()));
                    }
                    res.putAll(groups.stream().collect(Collectors.toMap(Group::getId,Group::getNameOfGroup)));
                }
                else{
                    List<Group> groups = Optional.of(groupRepository.findByWorkGroup(user.getWorkGroup())).orElse(Collections.emptyList());
                    res = groups.stream().collect(Collectors.toMap(Group::getId,Group::getNameOfGroup));
                }

                response.setType(MapObjectType.allGroups);
                response.setStatus(MapStatus.success);
                response.setObject(new ArrayList(res.entrySet()));
                return response;
            }
            case WorkGroup:
                break;
            case Organization:
                if(user.getRoles().contains(Role.ADMIN)){
                    MapResponseGetAll response = new MapResponseGetAll<Map.Entry<Long,String>>();
                    List<Organization> organizations = organizationRepo.findAll();
                    List<Organization4Map> organization4Maps = organizations.stream().map(organization -> new Organization4Map(organization.getId(), organization.getName()))
                            .collect(Collectors.toList());

                    response.setType(MapObjectType.ALL_ORGANIZATION);
                    response.setStatus(MapStatus.success);
                    response.setObject(organization4Maps);
                }
                break;

        }

        return null;
    }
}
