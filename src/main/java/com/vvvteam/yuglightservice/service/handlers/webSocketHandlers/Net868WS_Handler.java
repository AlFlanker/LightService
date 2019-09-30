package com.vvvteam.yuglightservice.service.handlers.webSocketHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.domain.entries.DataFromNet868;

import com.vvvteam.yuglightservice.service.UserService;
import com.vvvteam.yuglightservice.service.interfaces.AnalizeDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Scope(value = "session")
public class Net868WS_Handler extends AbstractWebSocketHandler {

    private final AnalizeDataService analizeDataService;
    private final UserService userService;

    @Autowired
    public Net868WS_Handler(AnalizeDataService analizeDataService, UserService userService) {
        this.analizeDataService = analizeDataService;
        this.userService = userService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user=userService.getUserByUserDetails((UserDetails) auth.getPrincipal());

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<DataFromNet868> jsonData = new ArrayList<>();
        jsonData.addAll(objectMapper.readValue(message.getPayload(), typeFactory.
                constructCollectionType(List.class, DataFromNet868.class)));
        analizeDataService.addNewDataNet868(jsonData,user.getOrganizationOwner());
    }
}
