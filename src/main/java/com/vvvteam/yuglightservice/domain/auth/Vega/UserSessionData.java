package com.vvvteam.yuglightservice.domain.auth.Vega;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Scope("singleton")
@Component("UserSessionData")
public class UserSessionData {
    private Map<Long,WebSocketSession> sessions = new ConcurrentHashMap<>();

    private Map<WebSocketSession,String> token4Session = new ConcurrentHashMap<>();

    public Map<Long, WebSocketSession> getSessions() {
        return sessions;
    }

    public void setSessions(Map<Long, WebSocketSession> sessions) {
        this.sessions = sessions;
    }

    public Map<WebSocketSession, String> getToken4Session() {
        return token4Session;
    }

    public void setToken4Session(Map<WebSocketSession, String> token4Session) {
        this.token4Session = token4Session;
    }

}
