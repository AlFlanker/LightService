package com.vvvteam.yuglightservice.service.factoryConnectionsUtils.interfaces;

import com.vvvteam.yuglightservice.domain.auth.NetData;
import com.vvvteam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.RestConnect;
import com.vvvteam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.WebSocketConnect;

public interface ConnectFactory {
     RestConnect getRestConnect(NetData data);
     WebSocketConnect getWebSocketConnect(NetData data);
}
