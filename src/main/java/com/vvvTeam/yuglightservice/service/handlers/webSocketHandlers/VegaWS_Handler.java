package com.vvvTeam.yuglightservice.service.handlers.webSocketHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.vvvTeam.yuglightservice.domain.auth.Vega.UserSessionData;
import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import com.vvvTeam.yuglightservice.exceptions.VegaRxDataException;
import com.vvvTeam.yuglightservice.service.interfaces.AnalizeDataService;
import com.vvvTeam.yuglightservice.service.interfaces.VegaMapService;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.VegaExecutable;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.VegaResp;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Scope(value = "session")
public class VegaWS_Handler extends AbstractWebSocketHandler {
    private final GenericWebApplicationContext context;
    private final AnalizeDataService dataService;
    private final VegaMapService vegaMapService;

    /**
     * Хандлер сообщений
     * Логика инкапсулированна в бины
     * @param session
     * @param message
     * @throws Exception
     */

        /*
        ToDo: после отладки убрать блоки if
        привести к виду:
        currentBean = context.getBean(VegaAuthResp.class);
                BeanUtils.copyProperties(vegaResp, currentBean);
                currentBean.execute(session);
         */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        VegaResp vegaResp;
        try {
            vegaResp = objectMapper.readValue(message.getPayload(), VegaResp.class);

            if (vegaResp instanceof VegaPong) {
                log.info("pong from " + session.getRemoteAddress());
            }

            if (vegaResp instanceof VegaAuthResp) {
                log.info("VegaWS_Handler is VegaAuthResp ");
                log.info(vegaResp.toString());
                VegaAuthResp currentBean = context.getBean(VegaAuthResp.class);
                BeanUtils.copyProperties(vegaResp, currentBean);
                currentBean.execute(session);
            }
            if (vegaResp instanceof VegaAllDeviceAppDataResp) {
                log.info("VegaWS_Handler is VegaAllDeviceAppDataResp ");
                log.info(vegaResp.toString());

            }
            if (vegaResp instanceof VegaRxDataResp) {
                log.info("VegaWS_Handler is VegaRxDataResp ");
                log.info(((VegaRxDataResp) vegaResp).getDevEui() + " -> " + ((VegaRxDataResp) vegaResp).getData());
                VegaRxDataResp currentBean = context.getBean(VegaRxDataResp.class);
                BeanUtils.copyProperties(vegaResp, currentBean);
                currentBean.setAnalizeDataService(dataService);
                currentBean.execute(session);
            }
            if (vegaResp instanceof VegaGetDataResp) {
                log.info(vegaResp.toString());
            }

            if(vegaResp instanceof VegaGetDevicesResp){
                log.info(((VegaGetDevicesResp)vegaResp).toString());
                List<Lamp> lamps = vegaMapService.addObjectsFromMap((VegaGetDevicesResp) vegaResp, null).get();
                log.info("qweqw");
            }
        }catch (VegaRxDataException e){
            log.info(e.toString());
        }catch (InvalidTypeIdException e) {
            log.info(e.toString());
        }


    }

}
