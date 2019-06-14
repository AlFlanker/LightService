package com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.interfaces.factories;

import com.vvvTeam.yuglightservice.domain.auth.NetData;
import com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.RestConnect;

public interface RestFactoryConnect {
    RestConnect setNetData(NetData netData);
}
