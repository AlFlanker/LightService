package com.vvvTeam.yuglightservice.repositories;

import com.vvvTeam.yuglightservice.domain.entries.Group;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.domain.entries.WorkGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupRepository extends CrudRepository<Group,Long> {
    Group findByNameOfGroup(String name);
    List<Group>findByNameOfGroupAndUserOwner(String name,User user);
    List<Group> findByUserOwner(User user);
    List<Group> findByWorkGroup(WorkGroup group);



}
