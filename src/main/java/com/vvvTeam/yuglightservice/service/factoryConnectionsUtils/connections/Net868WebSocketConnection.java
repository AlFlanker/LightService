package com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.connections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvvTeam.yuglightservice.domain.auth.NetData;
import com.vvvTeam.yuglightservice.domain.auth.Vega.UserSessionData;
import com.vvvTeam.yuglightservice.exceptions.BadNetDataException;
import com.vvvTeam.yuglightservice.service.UserService;
import com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.WebSocketConnect;
import com.vvvTeam.yuglightservice.service.handlers.webSocketHandlers.Net868WS_Handler;
import com.vvvTeam.yuglightservice.service.interfaces.AnalizeDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Slf4j
@Scope("prototype")
@Component
public class Net868WebSocketConnection implements WebSocketConnect {
    private final UserSessionData userSessionData;
    private final AnalizeDataService analizeDataService;
    private final UserService userService;
    private NetData auth_data;

    public Net868WebSocketConnection(UserSessionData userSessionData, AnalizeDataService analizeDataService, UserService userService) {
        this.userSessionData = userSessionData;
        this.analizeDataService = analizeDataService;
        this.userService = userService;
    }

    @Override
    public void connect() {
        ObjectMapper objectMapper = new ObjectMapper();
        WebSocketSession webSocketSession = userSessionData.getSessions().get(auth_data.getOwner().getId());
        try {
            if (Objects.isNull(webSocketSession)) {
                connectToNet868Serv(auth_data);
            } else if (Objects.nonNull(webSocketSession )) {
                if (!webSocketSession.isOpen()) {
                    connectToNet868Serv(auth_data);
                }
            }
        } catch (InterruptedException | ExecutionException | BadNetDataException e) {
            log.warn(e.getMessage());
        }
    }


    public void setNetData(NetData netData) {
        this.auth_data = netData;
    }

    private void connectToNet868Serv(NetData auth_data) throws ExecutionException, InterruptedException {
        WebSocketSession webSocketSession = userSessionData.getSessions().get(auth_data.getOwner().getId());
        WebSocketClient client = new StandardWebSocketClient();
        if (StringUtils.isEmpty(auth_data.getWsAddress()))
            throw new BadNetDataException("ws addres is emplty " + auth_data.getWsAddress());
        ListenableFuture<WebSocketSession> future = client.doHandshake(getNet868handler(), auth_data.getWsAddress() + "?token=" + auth_data.getToken());
        WebSocketSession socketSession = future.get();
        userSessionData.getSessions().put(auth_data.getOwner().getId(), socketSession);
    }

    @Bean
    Net868WS_Handler getNet868handler() {
        return new Net868WS_Handler(analizeDataService, userService);
    }
}
