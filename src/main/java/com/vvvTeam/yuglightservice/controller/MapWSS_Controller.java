package com.vvvTeam.yuglightservice.controller;

import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.service.UserService;
import com.vvvTeam.yuglightservice.service.handlers.UpdateEventHandler;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.MapExecutable;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.MapAddObj;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.MapGetAll;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.MapGetAllOrg;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.MapGetAllWG;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapRequest;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapType;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.MapResponseGetAll;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.MapResponseUnknown;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapObjectType;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapStatus;
import com.vvvTeam.yuglightservice.service.request.and.response.Request.LampPropsMsg;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.BaseObj4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.ResponseObjects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Controller
public class MapWSS_Controller {
    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate messagingTemplate;
    private final GenericWebApplicationContext context;
    private final UserService userService;



    @MessageMapping("/changeMessage")
    @SendToUser("/topic/activity")
    public ResponseObjects greeting(@AuthenticationPrincipal User user, @Payload LampPropsMsg message) throws Exception {
        return null;
    }

    /**
     * Обработка событий ламп
     * логика инкапсулирвоана в бины
     */
    @Transactional
    @MessageMapping("/lamp")
    @SendToUser("/lamp/events")
    public MapResponse wsLampmethod(@AuthenticationPrincipal User user, @Payload MapRequest message) throws Exception {
        MapExecutable execut;
        try {
            if (Objects.nonNull(message)) {
                MapExecutable contextBean = (MapExecutable) context.getBean(message.getClass());
                BeanUtils.copyProperties(message, contextBean);
                return contextBean.execute(user, MapType.Lamp);
            }
        } catch (NoSuchBeanDefinitionException e){
            log.warn(e.getMessage());
        }

        MapResponseUnknown unknown = new MapResponseUnknown();
        unknown.setType(MapObjectType.Unknown_request);
        return unknown;

    }

    /**
     * ToDo:Подумать в какой сервис убрать
     * Рассылка спорадических данных
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    @Async
    @EventListener
    public void updateStates(UpdateEventHandler ev) throws Exception {
        MapResponseGetAll response = new MapResponseGetAll<BaseObj4Map>();

        final Set<SimpUser> users = simpUserRegistry.getUsers();
        Set<String> usersNameByWorkGroups = userService
                .getUsersNameByWorkGroups(Collections.singleton(ev.getWorkGroup()));
        response.setType(MapObjectType.allLamps);
        response.setStatus(MapStatus.sporadically);
        response.setObject(Collections.singletonList(ev.getResponse()));
        for (String nick : usersNameByWorkGroups) {
            if (simpUserRegistry.getUser(nick) != null) {
                messagingTemplate.convertAndSendToUser(nick, "/lamp/events", response);
            }
        }

    }


    /**
     * Обработка событий КП
     * ToDo: после отладки и выбора функционала привести к виду @link wsLampmethod
     */
    @Transactional
    @MessageMapping("/cp")
    @SendToUser("/cp/events")
    public MapResponse wsCPmethod(@AuthenticationPrincipal User user, @Payload MapRequest message) throws Exception {
        MapExecutable execut;
        if (message instanceof MapGetAll) {
            execut = context.getBean(MapGetAll.class);
            BeanUtils.copyProperties(message, execut);
            return execut.execute(user, MapType.CP);
        } else if (message instanceof MapAddObj) {
            execut = context.getBean(MapAddObj.class);
            BeanUtils.copyProperties(message, execut);
            return execut.execute(user, MapType.CP);
        }
        MapResponseUnknown unknown = new MapResponseUnknown();
        unknown.setType(MapObjectType.Unknown_request);
        return unknown;
    }

    /**
     *Обработка событий групп
     * ToDo: после отладки и выбора функционала привести к виду @link wsLampmethod
     */
    @Transactional
    @MessageMapping("/groups")
    @SendToUser("/groups/events")
    public MapResponse wsGroupsmethod(@AuthenticationPrincipal User user, @Payload MapRequest message) throws Exception {
        MapExecutable execut;
        if (message instanceof MapGetAll) {
            execut = context.getBean(MapGetAll.class);
            BeanUtils.copyProperties(message, execut);
            return execut.execute(user, MapType.Group);
        } else if (message instanceof MapAddObj) {
            execut = context.getBean(MapAddObj.class);
            BeanUtils.copyProperties(message, execut);
            return execut.execute(user, MapType.Group);
        }
        MapResponseUnknown unknown = new MapResponseUnknown();
        unknown.setType(MapObjectType.Unknown_request);
        return unknown;
    }

    /**
     *Обработка событий рабочих групп
     * ToDo: после отладки и выбора функционала привести к виду @link wsLampmethod
     */
    @Transactional
    @MessageMapping("/wg")
    @SendToUser("/wg/events")
    public MapResponse wsWGMethod(@AuthenticationPrincipal User user, @Payload MapRequest message) throws Exception{
        MapExecutable execut;
        if (message instanceof MapGetAllWG) {
            execut = context.getBean(MapGetAllWG.class);
            BeanUtils.copyProperties(message, execut);
            return execut.execute(user, MapType.WorkGroup);
        }
        MapResponseUnknown unknown = new MapResponseUnknown();
        unknown.setType(MapObjectType.Unknown_request);
        return unknown;
    }

    /**
     *Обработка событий организаций
     * ToDo: после отладки и выбора функционала привести к виду @link wsLampmethod
     */
    @Transactional
    @MessageMapping("/org")
    @SendToUser("/org/events")
    public MapResponse wsOrganizationMethod(@AuthenticationPrincipal User user, @Payload MapRequest message) throws Exception{
        MapExecutable execut;
        if (message instanceof MapGetAllOrg) {
            execut = context.getBean(MapGetAllOrg.class);
            BeanUtils.copyProperties(message, execut);
            return execut.execute(user, MapType.Organization);
        }
        MapResponseUnknown unknown = new MapResponseUnknown();
        unknown.setType(MapObjectType.Unknown_request);
        return unknown;
    }

}
