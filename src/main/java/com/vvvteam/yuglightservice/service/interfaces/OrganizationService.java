package com.vvvteam.yuglightservice.service.interfaces;

import com.vvvteam.yuglightservice.domain.DTO.OrganizationInfo;
import com.vvvteam.yuglightservice.domain.DTO.registrateDTO.OrganizationRegistrate;
import com.vvvteam.yuglightservice.domain.auth.Organization;

import java.util.List;

public interface OrganizationService {
    void registrateOrganization(OrganizationRegistrate registrate);
    List<Organization> getAll();
    List<OrganizationInfo> getOrganizationInfo();

}
