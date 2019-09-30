package com.vvvteam.yuglightservice.service.factoryConnectionsUtils.interfaces.factories;

import com.vvvteam.yuglightservice.domain.auth.NetData;
import com.vvvteam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.WebSocketConnect;

public interface WebSocketFactoryConnect {
    WebSocketConnect setNetData(NetData netData);
}
