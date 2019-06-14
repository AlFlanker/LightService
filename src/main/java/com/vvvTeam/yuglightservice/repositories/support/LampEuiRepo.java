package com.vvvTeam.yuglightservice.repositories.support;

import com.vvvTeam.yuglightservice.domain.support.LampByEui;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface LampEuiRepo extends CrudRepository<LampByEui,String> {
}
