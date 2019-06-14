package com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vvvTeam.yuglightservice.exceptions.VegaRxDataException;
import com.vvvTeam.yuglightservice.service.interfaces.AnalizeDataService;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.VegaExecutable;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.VegaResp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Slf4j
@Scope("prototype")
@Component
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class VegaRxDataResp extends VegaResp {
    private String devEui;
    private String appEui;
    private Date ts;
    private String data;

    private AnalizeDataService analizeDataService;


    public void setAnalizeDataService(AnalizeDataService analizeDataService) {
        this.analizeDataService = analizeDataService;
    }

    @Override
    public void execute(WebSocketSession session) {
        try {
            analizeDataService.updateStates(this);
        }catch (VegaRxDataException e){
            log.warn(e.getMessage());
        }
    }




}
