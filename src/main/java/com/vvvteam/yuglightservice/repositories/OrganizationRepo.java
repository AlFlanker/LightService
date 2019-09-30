package com.vvvteam.yuglightservice.repositories;

import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepo extends JpaRepository<Organization, Long> {
    @Override
    Optional<Organization> findById(Long aLong);

    Optional<Organization> findByName(String name);
    Optional<Organization> findByNameAndIsDeleted(String name,boolean deleted);

    Optional<Organization> findByGroupFounder(User user);
}
