package com.vvvteam.yuglightservice.service.factoryConnectionsUtils.factories;

import com.vvvteam.yuglightservice.domain.auth.NetData;
import com.vvvteam.yuglightservice.domain.auth.TypeOfService;
import com.vvvteam.yuglightservice.service.factoryConnectionsUtils.connections.Net868RestConnection;
import com.vvvteam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.RestConnect;
import com.vvvteam.yuglightservice.service.factoryConnectionsUtils.interfaces.factories.RestFactoryConnect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;

@RequiredArgsConstructor
@Component
public class RestFactoryConnectImpl implements RestFactoryConnect {
    private final GenericWebApplicationContext context;




    @Override
    public RestConnect setNetData(NetData netData) {
        if(StringUtils.isEmpty(netData.getAddress())) throw new IllegalArgumentException("wss not be null!");
        if(netData.getTypeOfService().equals(TypeOfService.Vega)){
            throw new IllegalArgumentException("not support service");
        }
        else if(netData.getTypeOfService().equals(TypeOfService.Net868)){
            Net868RestConnection connection = context.getBean(Net868RestConnection.class);
            connection.setNetData(netData);
            return connection;

        }
        else throw new IllegalArgumentException("not support service");
    }


}
