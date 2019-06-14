package com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.factories;

import com.vvvTeam.yuglightservice.domain.auth.NetData;
import com.vvvTeam.yuglightservice.domain.auth.TypeOfService;
import com.vvvTeam.yuglightservice.domain.auth.Vega.UserSessionData;
import com.vvvTeam.yuglightservice.service.UserService;
import com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.connections.Net868WebSocketConnection;
import com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.connections.VegaWebSocketConnection;
import com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.WebSocketConnect;
import com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.interfaces.factories.WebSocketFactoryConnect;
import com.vvvTeam.yuglightservice.service.interfaces.AnalizeDataService;
import com.vvvTeam.yuglightservice.service.interfaces.VegaMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
@RequiredArgsConstructor
@Component
public class WebSocketFactoryConnectImpl implements WebSocketFactoryConnect {
    private final GenericWebApplicationContext context;

    @Override
    public WebSocketConnect setNetData(NetData netData) throws IllegalArgumentException{
        if(StringUtils.isEmpty(netData.getAddress())) throw new IllegalArgumentException("wss not be null!");

        if(netData.getTypeOfService().equals(TypeOfService.Vega)){
            VegaWebSocketConnection  webSocketConnect= context.getBean(VegaWebSocketConnection.class);
            webSocketConnect.setNetData(netData);
            return webSocketConnect;
        }
        else if(netData.getTypeOfService().equals(TypeOfService.Net868)){
            Net868WebSocketConnection webSocketConnect = context.getBean(Net868WebSocketConnection.class);
            webSocketConnect.setNetData(netData);
            return webSocketConnect;
        }
        else throw new IllegalArgumentException("not support service");
    }


}
