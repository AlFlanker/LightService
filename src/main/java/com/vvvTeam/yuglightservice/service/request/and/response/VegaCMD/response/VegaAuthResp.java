package com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.VegaExecutable;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.VegaResp;
import com.vvvTeam.yuglightservice.domain.auth.Vega.UserSessionData;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
@Slf4j
@Scope("prototype")
@Component
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(of = {"status","err_string","token","command_list"})
public class VegaAuthResp extends VegaResp {
    @Getter @Setter
    private String status;
    @Getter @Setter
    private String err_string;
    @Getter @Setter
    private String token;
    @Getter @Setter
    private List<String> command_list;

    @Getter @Setter @JsonIgnore
    private Long organizationId;
    @JsonIgnore
    @Autowired
    private UserSessionData userSessionData;

    @Override
    public void execute(WebSocketSession session) {
        for(Map.Entry<Long,WebSocketSession> entry:userSessionData.getSessions().entrySet()){
            if(entry.getValue().equals(session)){
                organizationId = entry.getKey();
                break;
            }
        }
        if(Boolean.parseBoolean(this.status)) {
            userSessionData.getToken4Session().put(session, this.token);
            ObjectMapper mapper = new ObjectMapper();
//            try {
//                this.session.sendMessage(new TextMessage(mapper.writeValueAsString(this)));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}
