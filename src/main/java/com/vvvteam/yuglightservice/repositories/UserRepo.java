package com.vvvteam.yuglightservice.repositories;

import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.domain.entries.WorkGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByUsername(String username);
    User findByUsernameAndIsDeleted(String username,boolean deleted);
    User findByUsernameAndPassword(String username,String password);
    User findById(long id);
    User findByActivationCode(String code);
    List<User> findAll();
    List<User> findByOrganizationOwner(Organization organization);
    long countByWorkGroup(WorkGroup group);
    long countByWorkGroupAndIsDeleted(WorkGroup group,boolean deleted);
    List<User> findByWorkGroup(WorkGroup group);
    List<User> findByWorkGroupAndIsDeleted(WorkGroup group,boolean deleted);

    int countByOrganizationOwnerAndIsDeleted(Organization organization, boolean b);
}
