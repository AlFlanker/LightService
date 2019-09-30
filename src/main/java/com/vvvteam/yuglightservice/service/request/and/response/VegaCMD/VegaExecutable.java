package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD;

import org.springframework.web.socket.WebSocketSession;

public interface VegaExecutable {
    void execute(WebSocketSession session);
    void execute();

}
