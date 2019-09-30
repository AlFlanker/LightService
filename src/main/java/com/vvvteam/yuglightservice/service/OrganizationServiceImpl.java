package com.vvvteam.yuglightservice.service;

import com.vvvteam.yuglightservice.domain.DTO.OrganizationInfo;
import com.vvvteam.yuglightservice.domain.DTO.registrateDTO.OrganizationRegistrate;
import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.exceptions.organization.OrganizationAlreadyExist;
import com.vvvteam.yuglightservice.exceptions.user.UserAlreadyExistException;
import com.vvvteam.yuglightservice.repositories.ControlPointsRepo;
import com.vvvteam.yuglightservice.repositories.LampRepository;
import com.vvvteam.yuglightservice.repositories.OrganizationRepo;
import com.vvvteam.yuglightservice.repositories.UserRepo;
import com.vvvteam.yuglightservice.repositories.WorkGroupRepo;
import com.vvvteam.yuglightservice.service.interfaces.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepo organizationRepo;
    private final UserService userService;
    private final WorkGroupRepo groupRepo;
    private final ControlPointsRepo controlPointsRepo;
    private final LampRepository lampRepository;
    private final UserRepo userRepo;

    /**
     * Зарегистрировать Админа организации (и организации)
     * @param registrate
     * @throws OrganizationAlreadyExist
     * @throws UserAlreadyExistException
     */
    @Override
    public void registrateOrganization(OrganizationRegistrate registrate) throws OrganizationAlreadyExist, UserAlreadyExistException {
        User user = new User();
        user.setUsername(registrate.getUsername());
        user.setPassword(registrate.getPassword());
        user.setEmail(registrate.getEmail());
        if (organizationRepo.findByNameAndIsDeleted(registrate.getOrganization().trim().toLowerCase(),false).isPresent()) {
            throw new OrganizationAlreadyExist("organization " + registrate.getOrganization() + " already exist!");
        } else {
            Organization organization = new Organization();
            organization.setName(registrate.getOrganization().trim().toLowerCase());
            userService.addUser(user, organization);
        }
    }

    /**
     * Запрос всех организации
     * @return List<Organization>
     */
    @Override
    public List<Organization> getAll() {
        return Optional.of(organizationRepo.findAll()).orElse(Collections.emptyList());
    }

    /**
     * Запрос информации о организациях.
     * @return DTO
     */
    @Transactional
    @Override
    public List<OrganizationInfo> getOrganizationInfo() {
        List<Organization> organizations = getAll();
        List<OrganizationInfo> infoList = new ArrayList<>();
        for (Organization org : organizations) {
            infoList.add(getOrgInfo(org));
        }
        return infoList;
    }

    /**
     * Конвертер Organization -> DTO
     * @param organization
     * @return
     */
    @Transactional(readOnly = true)
    public OrganizationInfo getOrgInfo(Organization organization){
        OrganizationInfo info = new OrganizationInfo();
        info.setId(organization.getId());
        info.setName(organization.getName());
        info.setFounderUser(organization.getGroupFounder().getUsername());
        info.setWorkGroupCount(groupRepo.countByOrganizationOwnerAndIsDeleted(organization,false));
        info.setCpCount(controlPointsRepo.countByOrganizationOwnerAndIsDeleted(organization,false));
        info.setLampCount(lampRepository.countByOrganizationOwnerAndIsDeleted(organization,false));
        info.setUsersCount(userRepo.countByOrganizationOwnerAndIsDeleted(organization,false));
        return info;
    }
}
