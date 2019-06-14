package com.vvvTeam.yuglightservice.service.handlers;

import com.vvvTeam.yuglightservice.domain.entries.WorkGroup;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.MapResponseGetAll;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.BaseObj4Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
@Getter
@Setter
public class UpdateEventHandler extends ApplicationEvent {
    private WorkGroup workGroup;
    private BaseObj4Map response;
    public UpdateEventHandler(Object source) {
        super(source);
    }
}
