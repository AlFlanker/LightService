package com.vvvteam.yuglightservice.service.interfaces;

import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.domain.entries.ControlPoint;
import com.vvvteam.yuglightservice.domain.entries.WorkGroup;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.rest_api.AddControlPointFromMap;
import com.vvvteam.yuglightservice.service.request.and.response.Request.LampPropsMsg;
import com.vvvteam.yuglightservice.service.request.and.response.Responses.ResponceControlPoint;

import java.util.List;

public interface ControlPointService {

    long countByWorkGroup(WorkGroup group, boolean deleted);
    ControlPoint addNewKP(User user, LampPropsMsg message);
    ControlPoint getById(long id);
    List<ResponceControlPoint> getCP4Map(List<ControlPoint> cps);
    ResponceControlPoint addControlPointFromMap(User user, AddControlPointFromMap cp);
}
