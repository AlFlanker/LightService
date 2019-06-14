package com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.MapExecutable;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.Support.PassedObj;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapRequest;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapType;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.service.interfaces.StateService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Scope("prototype")
@Component
@NoArgsConstructor
public class MapGetAllPassedObjStates extends MapRequest implements MapExecutable {
    @JsonIgnore
    private StateService stateService;
    @Autowired
    public void setStateService(StateService stateService) {
        this.stateService = stateService;
    }

    @Getter @Setter
    private List<PassedObj> payload;
    @Override
    public MapResponse execute(User user, MapType type) {
        switch (type) {
            case Lamp: {
                stateService.Passed(payload);
            }
            case CP:{

            }

        }
        return null;
    }
}
