package com.vvvteam.yuglightservice.service.factoryConnectionsUtils.factories;

import com.vvvteam.yuglightservice.domain.auth.NetData;
import com.vvvteam.yuglightservice.service.factoryConnectionsUtils.interfaces.ConnectFactory;
import com.vvvteam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.RestConnect;
import com.vvvteam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.WebSocketConnect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * Фабрика фабрик.
 * Создает объект RestConnect или WebSocketConnect
 */
@RequiredArgsConstructor
@Service
public class SimpleConnectFactory implements ConnectFactory {
    private final RestFactoryConnectImpl restFactoryConnect;
    private final WebSocketFactoryConnectImpl webSocketFactoryConnect;

    /**
     *
     * @param data
     * @return объект RestConnect
     * @throws IllegalArgumentException, если нет возможности создавть соединение
     */
    @Override
    @Deprecated
    public RestConnect getRestConnect(NetData data) throws IllegalArgumentException {
        return restFactoryConnect.setNetData(data);
    }

    /**
     *
     * @param data
     * @return WebSocketConnect
     * @throws IllegalArgumentException, если нет возможности создавть соединение
     */
    @Override
    public WebSocketConnect getWebSocketConnect(NetData data) throws IllegalArgumentException{
           return webSocketFactoryConnect.setNetData(data);
    }
}
