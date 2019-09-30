package com.vvvteam.yuglightservice.repositories.support;

import com.vvvteam.yuglightservice.domain.support.LampByEui;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface LampEuiRepo extends CrudRepository<LampByEui,String> {
}
