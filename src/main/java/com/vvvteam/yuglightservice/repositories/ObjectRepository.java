package com.vvvteam.yuglightservice.repositories;

import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.entries.Group;
import com.vvvteam.yuglightservice.domain.auth.Organization;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ObjectRepository extends CrudRepository<Lamp,Long> {
    List<Lamp> findByName(String name);
    List<Lamp> findByOrganizationOwner(Organization organization);
    List<Lamp> findByGroup(Group group);
}
