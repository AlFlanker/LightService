package com.vvvTeam.yuglightservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Используется Stomp поверх SockJS для поддержки клиентов за Прокси/Ваерволами
 */
@Slf4j
@EnableWebSocketMessageBroker
@Configuration
public class WebSocketSecurityConfig
        extends AbstractSecurityWebSocketMessageBrokerConfigurer {

//    private static final String SOCKJS_VERSION = "https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.3.0/sockjs.min.js";
    private static final String SOCKJS_VERSION = "https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js";


    /**
     * Настройка топиков по группам событий
     *
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic","/user","/lamp","/cp","/groups","/wg","/org");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");

    }
    /**
     * Задание точки подключения и версии SockJS
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /*HTTP URL for the endpoint to which a WebSocket (or SockJS) client needs to connect for the WebSocket handshake.*/
        registry.addEndpoint("/maps_events").setAllowedOrigins("*").withSockJS()
                .setClientLibraryUrl(SOCKJS_VERSION);



    }

    /**
     * Права доступа
     * На текущий момент разрешенны все подключения без авторизации.
     */
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        super.configureInbound(messages);
        messages
                .simpDestMatchers("/**").permitAll()
                .anyMessage().permitAll()
        .simpTypeMatchers(SimpMessageType.CONNECT,SimpMessageType.SUBSCRIBE,SimpMessageType.DISCONNECT).permitAll();
    }

    /*CSRF for websocket disable @return true*/
    /*Determines if a CSRF token is required for connecting.*/
    /*
    Returns:
    false if a CSRF token is required for connecting, else true*/
    @Override
    protected boolean sameOriginDisabled() {
        return false;
    }


}
