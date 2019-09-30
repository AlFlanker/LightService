package com.vvvteam.yuglightservice.repositories;

import com.vvvteam.yuglightservice.domain.auth.NetData;
import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.auth.TypeOfService;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NetDataRepo extends CrudRepository<NetData,Long> {
    List<NetData> findByOwner(Organization organization);
    NetData findByOwnerAndTypeOfService(Organization organization, TypeOfService service);
}
