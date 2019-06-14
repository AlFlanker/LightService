package com.vvvTeam.yuglightservice.service.interfaces;

import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import com.vvvTeam.yuglightservice.domain.entries.StateObj;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.Support.PassedObj;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface StateService {
    List<Object[]> getStatesBetween(Lamp lamp, Date start, Date stop, long limit, Set<String> tags);
    List<StateObj> getStatesBetween(Lamp lamp, Date start, Date stop, int limit);
    void Passed(List<PassedObj> data);
    List<StateObj> getNotPassedStates(Lamp obj);

}
