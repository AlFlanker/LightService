package com.vvvteam.yuglightservice.repositories;

import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.domain.entries.WorkGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkGroupRepo extends JpaRepository<WorkGroup, Long> {
    Optional<WorkGroup> findByName(String name);
    Optional<WorkGroup> findByNameAndIsDeleted(String name,boolean deleted);
    List<WorkGroup> findByOrganizationOwner(Organization organization);
    List<WorkGroup> findByOrganizationOwnerAndIsDeleted(Organization organization,boolean deleted);
    int countByOrganizationOwnerAndIsDeleted(Organization organization,boolean deleted);
    WorkGroup findByWgFounder(User user);



}
