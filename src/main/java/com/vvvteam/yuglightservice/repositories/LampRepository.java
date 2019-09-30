package com.vvvteam.yuglightservice.repositories;

import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.entries.Group;
import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.entries.WorkGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LampRepository extends CrudRepository<Lamp,Long> {
    Lamp findByName(String name);
    Lamp findByNameAndIsDeleted(String name,boolean deleted);
    List<Lamp> findByOrganizationOwner(Organization organization);
    List<Lamp> findByOrganizationOwnerAndIsDeleted(Organization organization,boolean deleted);
    List<Lamp> findByGroup(Group group);
    List<Lamp> findByWorkGroup(WorkGroup workGroup);
    List<Lamp> findByWorkGroupAndIsDeleted(WorkGroup workGroup,boolean deleted);
    Page<Lamp> findByWorkGroup(WorkGroup group, Pageable pageable);
    Optional<List<Lamp>> findByNameIn(List<String> nameList);
    @Query("SELECT lamp FROM Lamp lamp where lamp.id in :Ids")
    List<Lamp> findByArrId(@Param("Ids") List<Long> ids);
    Long countByWorkGroup(WorkGroup group);
    Long countByWorkGroupAndIsDeleted(WorkGroup group,boolean deleted);
    int countByOrganizationOwnerAndIsDeleted(Organization organization, boolean b);

}
