package com.vvvteam.yuglightservice.repositories.support;

import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.support.LampsLastStates;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LampsStatesRepo extends CrudRepository<LampsLastStates,Lamp> {
    List<LampsLastStates> findAll();
}
