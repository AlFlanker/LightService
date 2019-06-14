package com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.interfaces;

import com.vvvTeam.yuglightservice.domain.auth.NetData;
import com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.RestConnect;
import com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.WebSocketConnect;

public interface ConnectFactory {
     RestConnect getRestConnect(NetData data);
     WebSocketConnect getWebSocketConnect(NetData data);
}
