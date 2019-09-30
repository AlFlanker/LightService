package com.vvvteam.yuglightservice.service.factoryConnectionsUtils.interfaces.factories;

import com.vvvteam.yuglightservice.domain.auth.NetData;
import com.vvvteam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.RestConnect;

public interface RestFactoryConnect {
    RestConnect setNetData(NetData netData);
}
