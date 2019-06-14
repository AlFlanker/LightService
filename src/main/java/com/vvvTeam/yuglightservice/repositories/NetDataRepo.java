package com.vvvTeam.yuglightservice.repositories;

import com.vvvTeam.yuglightservice.domain.auth.NetData;
import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.auth.TypeOfService;
import com.vvvTeam.yuglightservice.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NetDataRepo extends CrudRepository<NetData,Long> {
    List<NetData> findByOwner(Organization organization);
    NetData findByOwnerAndTypeOfService(Organization organization, TypeOfService service);
}
