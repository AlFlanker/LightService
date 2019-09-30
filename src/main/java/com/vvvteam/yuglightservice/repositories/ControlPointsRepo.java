package com.vvvteam.yuglightservice.repositories;

import com.vvvteam.yuglightservice.domain.entries.ControlPoint;
import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.entries.WorkGroup;
import com.vvvteam.yuglightservice.domain.auth.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ControlPointsRepo extends JpaRepository<ControlPoint,Long> {
    ControlPoint findByObjectName(String name);
//    List<ControlPoint> findByWorkGroup(WorkGroup group);
    Page<ControlPoint> findByWorkGroup(WorkGroup group, Pageable pageable);
    List<ControlPoint> findByWorkGroupAndIsDeleted(WorkGroup group,boolean deleted);
    List<ControlPoint> findByOrganizationOwner(Organization organization);
    Long countByWorkGroup(WorkGroup group);
    Optional<ControlPoint> findByLampsIn(Set<Lamp> lamps);
    Long countByWorkGroupAndIsDeleted(WorkGroup group,boolean deleted);
    int countByOrganizationOwnerAndIsDeleted(Organization organization, boolean b);
}
