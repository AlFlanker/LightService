package com.vvvTeam.yuglightservice.service.interfaces;

import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.domain.entries.ControlPoint;
import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import com.vvvTeam.yuglightservice.domain.entries.WorkGroup;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.rest_api.AddLampFromMap;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.rest_api.UpdateLampFromMap;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.rest_api.UpdateLampProp;
import com.vvvTeam.yuglightservice.service.request.and.response.Request.LampPropsMsg;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.BaseObj4Map;

import java.text.ParseException;
import java.util.List;

public interface LampService {
    List<Lamp> getAllLampByWorkGroup(WorkGroup group);

    List<BaseObj4Map> getObjWithoutLastStates(List<Lamp> lamps);

    List<BaseObj4Map> getObj4MapWithLastStates(List<Lamp> lamps);

    List<BaseObj4Map> getObj4Map(List<Lamp> lamps);

    BaseObj4Map addNewObj(User user, LampPropsMsg message) throws ParseException;

    List<Lamp> findLampByInId(List<Long> ids);

    List<ControlPoint> findCPWorkGroupWithPage(WorkGroup workGroup);

    List<Object[]> findAllLamp(WorkGroup group);

    List<Object[]> findAllCP(WorkGroup group);

    long countLampByWorkGroup(WorkGroup group, boolean deleted);

    BaseObj4Map changeLampCP(Lamp lamp, ControlPoint controlPoint);

    Lamp getByEUI(String eui);

    Lamp updateLamp(UpdateLampProp properties);

    BaseObj4Map addLamp(AddLampFromMap lamp, User user);

    BaseObj4Map updateLamp(UpdateLampFromMap lamp, User user);

    void deleteLamp(String eui);


}
