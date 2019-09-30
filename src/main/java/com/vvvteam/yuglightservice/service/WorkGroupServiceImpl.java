package com.vvvteam.yuglightservice.service;

import com.vvvteam.yuglightservice.domain.DTO.WorkGroupInfo;
import com.vvvteam.yuglightservice.domain.DTO.registrateDTO.WorkGroupDTO;
import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.auth.Role;
import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.domain.entries.WorkGroup;
import com.vvvteam.yuglightservice.exceptions.workGroup.WorkGroupAlreadyExistException;
import com.vvvteam.yuglightservice.repositories.OrganizationRepo;
import com.vvvteam.yuglightservice.repositories.WorkGroupRepo;
import com.vvvteam.yuglightservice.service.interfaces.LampService;
import com.vvvteam.yuglightservice.service.interfaces.WorkGroupService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@AllArgsConstructor
@Service
public class WorkGroupServiceImpl implements WorkGroupService {
    private final WorkGroupRepo workGroupRepo;
    private final UserService userService;
    private final OrganizationRepo organizationRepo;
    private final ControlPointServiceImpl controlPointServiceImpl;
    private final LampService objectsService;


    /**
     *Создать рабочую группу с админом группы
     * @param currentUser - текущий юзер
     * @param registrate - данные для регистрации
     * @throws WorkGroupAlreadyExistException - если такая группа есть!
     */
    @Transactional
    @Override
    public void addWorkGroup(User currentUser, WorkGroupDTO registrate) throws WorkGroupAlreadyExistException {
        Organization organization;
        if (StringUtils.isEmpty(registrate.getOrganization()) && currentUser.getRoles().contains(Role.SuperUserOwner)) {
            organization = currentUser.getOrganizationOwner();
        } else {
            Optional<Organization> byId = organizationRepo.findById(Long.valueOf(registrate.getOrganization()));
            organization = byId.orElse(null);
        }

        if (workGroupRepo.findByNameAndIsDeleted(registrate.getWorkGroup().trim().toLowerCase(),false).isPresent()) {
            throw new WorkGroupAlreadyExistException(registrate.getWorkGroup() + " already exist");
        } else if (Objects.nonNull(organization)) {
            User user = new User();
            user.setUsername(registrate.getUsername());
            user.setPassword(registrate.getPassword());
            user.setEmail(registrate.getEmail());
            WorkGroup workGroup = new WorkGroup();
            workGroup.setName(registrate.getWorkGroup().trim().toLowerCase());
            userService.addUser(user, workGroup, organization);
        }
    }

    /**
     * Запрос информации о рабочих группах организации
     * @param organization
     * @return List<DTO>
     */
    @Override
    public List<WorkGroupInfo> getWorkGroupsInfo(Organization organization) {
        List<WorkGroupInfo> groupInfoList = new ArrayList<>();
        WorkGroupInfo workGroupInfo;
        List<WorkGroup> workGroups = workGroupRepo.findByOrganizationOwnerAndIsDeleted(organization,false);
        if (Objects.nonNull(workGroups)) {
            for (WorkGroup workGroup : workGroups) {
                workGroupInfo = new WorkGroupInfo();
                workGroupInfo.setId(workGroup.getId());
                workGroupInfo.setName(workGroup.getName());
                workGroupInfo.setWgFounder(workGroup.getWgFounder().getUsername());
                workGroupInfo.setCpCount(controlPointServiceImpl.countByWorkGroup(workGroup,false));
                workGroupInfo.setLampCount(objectsService.countLampByWorkGroup(workGroup,false));
                workGroupInfo.setUserCount(userService.countByWorkGroup(workGroup,false));
                groupInfoList.add(workGroupInfo);
            }
            return groupInfoList;
        } else
            return Collections.emptyList();
    }

    /**
     * Найти группу по админу
     * @param user
     * @return
     */
    @Override
    public WorkGroup getByUserFounder(User user) {
        return workGroupRepo.findByWgFounder(user);
    }

    /**
     * Запрос рабочих групп организации
     * @param organization
     * @return рабочие группы
     */
    @Override
    public List<WorkGroup> getAllByOrganization(Organization organization) {
        return workGroupRepo.findByOrganizationOwnerAndIsDeleted(organization,false);
    }
}
