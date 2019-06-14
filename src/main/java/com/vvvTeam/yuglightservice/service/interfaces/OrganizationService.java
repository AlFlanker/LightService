package com.vvvTeam.yuglightservice.service.interfaces;

import com.vvvTeam.yuglightservice.domain.DTO.OrganizationInfo;
import com.vvvTeam.yuglightservice.domain.DTO.registrateDTO.OrganizationRegistrate;
import com.vvvTeam.yuglightservice.domain.auth.Organization;

import java.util.List;

public interface OrganizationService {
    void registrateOrganization(OrganizationRegistrate registrate);
    List<Organization> getAll();
    List<OrganizationInfo> getOrganizationInfo();

}
