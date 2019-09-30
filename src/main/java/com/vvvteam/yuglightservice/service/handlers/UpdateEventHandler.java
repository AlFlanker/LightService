package com.vvvteam.yuglightservice.service.handlers;

import com.vvvteam.yuglightservice.domain.entries.WorkGroup;
import com.vvvteam.yuglightservice.service.request.and.response.Responses.BaseObj4Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UpdateEventHandler extends ApplicationEvent {
    private WorkGroup workGroup;
    private BaseObj4Map response;
    public UpdateEventHandler(Object source) {
        super(source);
    }
}
