package com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.auth.Vega.UserSessionData;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.VegaExecutable;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.VegaRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@Scope("prototype")
@Component
@RequiredArgsConstructor
public class VegaGetDevices extends VegaRequest implements VegaExecutable {
    @JsonIgnore
    private final UserSessionData userSessionData;
    @Getter
    @Setter
    @JsonIgnore
    private Organization organization;

    @Override
    public void execute() {
        WebSocketSession session=userSessionData.getSessions().get(organization.getId());
        ObjectMapper mapper = new ObjectMapper();
        if(session.isOpen()){
            try {
                session.sendMessage(new TextMessage(mapper.writeValueAsString(this)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void execute(WebSocketSession session) {
        return;
    }
}
