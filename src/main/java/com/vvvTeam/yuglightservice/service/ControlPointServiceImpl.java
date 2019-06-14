package com.vvvTeam.yuglightservice.service;

import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.auth.Role;
import com.vvvTeam.yuglightservice.domain.entries.ControlPoint;
import com.vvvTeam.yuglightservice.domain.entries.Location;
import com.vvvTeam.yuglightservice.domain.entries.WorkGroup;
import com.vvvTeam.yuglightservice.exceptions.organization.OrganizationNotFound;
import com.vvvTeam.yuglightservice.exceptions.workGroup.WorkGroupNotFoundException;
import com.vvvTeam.yuglightservice.repositories.OrganizationRepo;
import com.vvvTeam.yuglightservice.repositories.WorkGroupRepo;
import com.vvvTeam.yuglightservice.service.interfaces.ControlPointService;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.rest_api.AddControlPointFromMap;
import com.vvvTeam.yuglightservice.service.request.and.response.Request.LampPropsMsg;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.repositories.ControlPointsRepo;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.Organization4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.ResponceControlPoint;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.WorkGroup4Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ControlPointServiceImpl implements ControlPointService {
    private final ControlPointsRepo controlPointsRepo;
    private final OrganizationRepo organizationRepo;
    private final WorkGroupRepo workGroupRepo;



    @Override
    public long countByWorkGroup(WorkGroup group, boolean deleted) {
            return controlPointsRepo.countByWorkGroupAndIsDeleted(group,deleted);
    }

    @Transactional
    @Override
    public ControlPoint addNewKP(User user, LampPropsMsg message){
        if(controlPointsRepo.findByObjectName(message.getAlias())==null){
            ControlPoint controlPoint = new ControlPoint();
            controlPoint.setObjectName(message.getAlias());
            controlPoint.setLastUpdate(new Date());
            controlPoint.setLocation(new Location(Double.parseDouble(message.getLatitude()), Double.parseDouble(message.getLongitude()), ""));
            controlPoint.setOrganizationOwner(user.getOrganizationOwner());
            controlPoint.setWorkGroup(user.getWorkGroup());
            controlPoint.setDeleted(false);
            return controlPointsRepo.save(controlPoint);
        }
        return null;
    }


    @Override
    public ControlPoint getById(long id) {
        return controlPointsRepo.findById(id).orElse(null);
    }

    /**
     *
     * @param cps
     * @return конвертер List<ControlPoints> -> List<DTO>
     */
    @Transactional(readOnly = true)
    @Override
    public List<ResponceControlPoint> getCP4Map(List<ControlPoint> cps) {
        List<ResponceControlPoint> controlPoints = new ArrayList<>();
        Organization4Map org4Map;
        WorkGroup4Map workGroup4Map;
        ResponceControlPoint controlPoint;
        for(ControlPoint cp: cps){
            controlPoint = getResponceControlPoint(cp);
            controlPoints.add(controlPoint);
        }
        return controlPoints;
    }

    /**
     *
     * @param cp
     * @return конвертер ControlPoints -> DTO
     */
    @Transactional(readOnly = true)
    public ResponceControlPoint getResponceControlPoint(ControlPoint cp) {
        ResponceControlPoint controlPoint;
        Organization4Map org4Map;
        WorkGroup4Map workGroup4Map;
        controlPoint = new ResponceControlPoint(cp.getId(),cp.getObjectName(),cp.getLocation().getLatitude(),cp.getLocation().getLongitude());
        org4Map = new Organization4Map();
        workGroup4Map = new WorkGroup4Map();
        org4Map.setId(cp.getOrganizationOwner().getId());
        org4Map.setName(cp.getOrganizationOwner().getName());
        controlPoint.setOrganization(org4Map);
        workGroup4Map.setId(cp.getWorkGroup().getId());
        workGroup4Map.setName(cp.getWorkGroup().getName());
        controlPoint.setWorkGroup(workGroup4Map);
        return controlPoint;
    }


    /**
     * Добавление нового КП
     * @param user
     * @param cp
     * @return КП DTO
     */
    @Transactional
    @Override
    public ResponceControlPoint addControlPointFromMap(User user, AddControlPointFromMap cp) {
        ControlPoint new_cp = new ControlPoint();
        Organization organization=null;
        WorkGroup workGroup = null;

        if(Objects.nonNull(controlPointsRepo.findByObjectName(cp.getName()))) return null;
        if (user.getRoles().contains(Role.ADMIN)) {
            if (Objects.nonNull(cp.getOrganization()) && cp.getOrganization() > 0) {
                Optional<Organization> organizationOpt = organizationRepo.findById(cp.getOrganization());
                if (organizationOpt.isPresent()) {
                    organization = organizationOpt.get();
                } else throw new OrganizationNotFound("");
            }
            workGroup = workGroupRepo.findById(cp.getWorkGroup()).orElseThrow(() -> new WorkGroupNotFoundException(""));
        }
        else organization= user.getOrganizationOwner();
        if (user.getRoles().contains(Role.SuperUserOwner)) {
            workGroup = workGroupRepo.findById(cp.getWorkGroup()).orElseThrow(() -> new WorkGroupNotFoundException(""));
        }
        else if(user.getRoles().contains(Role.SuperUser)) workGroup = user.getWorkGroup();
        new_cp.setWorkGroup(workGroup);
        new_cp.setOrganizationOwner(organization);
        new_cp.setObjectName(cp.getName());
        Location location = new Location();
        location.setLatitude(cp.getLat());
        location.setLongitude(cp.getLon());
        new_cp.setLocation(location);
        ControlPoint point = controlPointsRepo.save(new_cp);
        return getResponceControlPoint(point);
    }
}
