package com.vvvTeam.yuglightservice.service.interfaces;

import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.domain.entries.ControlPoint;
import com.vvvTeam.yuglightservice.domain.entries.WorkGroup;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.rest_api.AddControlPointFromMap;
import com.vvvTeam.yuglightservice.service.request.and.response.Request.LampPropsMsg;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.ResponceControlPoint;

import java.util.List;

public interface ControlPointService {

    long countByWorkGroup(WorkGroup group, boolean deleted);
    ControlPoint addNewKP(User user, LampPropsMsg message);
    ControlPoint getById(long id);
    List<ResponceControlPoint> getCP4Map(List<ControlPoint> cps);
    ResponceControlPoint addControlPointFromMap(User user, AddControlPointFromMap cp);
}
