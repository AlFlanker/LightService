package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.VegaRequest;
import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.auth.Vega.UserSessionData;
import lombok.*;
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
@NoArgsConstructor
@ToString(of = {"login","password"})
public class VegaAuth extends VegaRequest {
    @Getter @Setter
    private String login;
    @Getter @Setter
    private String password;
    @Getter @Setter @JsonIgnore
    private Organization organization;

    @JsonIgnore
    @Autowired
    private UserSessionData userSessionData;




    public void execute() {
        WebSocketSession session=userSessionData.getSessions().get(organization.getId());
        ObjectMapper mapper = new ObjectMapper();
        if(session.isOpen()){
            try {
                log.info("VegaAuth: message -> " + mapper.writeValueAsString(this));
                session.sendMessage(new TextMessage(mapper.writeValueAsString(this)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
