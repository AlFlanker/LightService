package com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.connections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvvTeam.yuglightservice.domain.auth.NetData;
import com.vvvTeam.yuglightservice.domain.auth.Vega.UserSessionData;

import com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.WebSocketConnect;
import com.vvvTeam.yuglightservice.service.handlers.webSocketHandlers.VegaWS_Handler;
import com.vvvTeam.yuglightservice.service.interfaces.AnalizeDataService;
import com.vvvTeam.yuglightservice.service.interfaces.VegaMapService;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.requests.VegaAuth;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.requests.VegaGetDevices;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.requests.VegaPing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Slf4j
@Scope("prototype")
@Component
public class VegaWebSocketConnection implements WebSocketConnect {
    private final UserSessionData userSessionData;
    private final GenericWebApplicationContext context;
    private final AnalizeDataService dataService;
    private final VegaMapService vegaMapService;
    private NetData auth_data;
    @Autowired
    public VegaWebSocketConnection(UserSessionData userSessionData, GenericWebApplicationContext context, AnalizeDataService dataService, VegaMapService vegaMapService) {
        this.userSessionData = userSessionData;
        this.context = context;
        this.dataService = dataService;
        this.vegaMapService = vegaMapService;
    }


    public void setNetData(NetData netData) {
        this.auth_data = netData;
    }

    @Override
    public void connect() {
        ObjectMapper objectMapper = new ObjectMapper();
        WebSocketSession webSocketSession = userSessionData.getSessions().get(auth_data.getOwner().getId());
        try {
            if (Objects.isNull(webSocketSession)) {
                connectToVegaServ(auth_data);
            }
            if ((Objects.nonNull(webSocketSession))) {
                if (!webSocketSession.isOpen()) {
                    connectToVegaServ(auth_data);
                } else {
                    webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(new VegaPing())));
                    //Запрос всех устройств
//                    VegaGetDevices bean = context.getBean(VegaGetDevices.class);
//                    bean.setOrganization(auth_data.getOwner());
//                    bean.execute();
                }
            }
        }
        catch (InterruptedException | ExecutionException | IOException  e){
            log.warn(e.getMessage());
        }
    }



    private void connectToVegaServ(NetData auth_data) throws InterruptedException, ExecutionException {
        WebSocketClient client = new StandardWebSocketClient();
        ListenableFuture<WebSocketSession> future = client.doHandshake(getVegaWS_Handler(), auth_data.getAddress());
        WebSocketSession socketSession = future.get();
        userSessionData.getSessions().put(auth_data.getOwner().getId(), socketSession);
        VegaAuth auth = context.getBean(VegaAuth.class);
        auth.setLogin(auth_data.getLogin());
        auth.setPassword(auth_data.getPassword());
        auth.setOrganization(auth_data.getOwner());
        auth.execute();
    }
    @Bean
    VegaWS_Handler getVegaWS_Handler() {
        return new VegaWS_Handler(context, dataService,vegaMapService);
    }

    @PreDestroy
    public void preDestroy() {
       log.info(this.getClass().getSimpleName() + " bean has been destroy");
    }

}
