package com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.interfaces.factories;

import com.vvvTeam.yuglightservice.domain.auth.NetData;
import com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.WebSocketConnect;

public interface WebSocketFactoryConnect {
    WebSocketConnect setNetData(NetData netData);
}
