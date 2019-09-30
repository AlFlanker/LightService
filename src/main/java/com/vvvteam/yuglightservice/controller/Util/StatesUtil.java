package com.vvvteam.yuglightservice.controller.Util;

import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.entries.StateObj;
import com.vvvteam.yuglightservice.service.request.and.response.Responses.State2JSON;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatesUtil {
    public static List<State2JSON> getJSONStates(List<StateObj> list){
        List<State2JSON> result = new ArrayList<>();
        State2JSON state2JSON;
        for(StateObj obj:list){
            state2JSON = new State2JSON();
            state2JSON.setObjAlias("".equals(obj.getLamp().getAlias())?obj.getLamp().getName():obj.getLamp().getAlias());
            state2JSON.setCurrentDate(obj.getCurrentDate());
            state2JSON.setVAc(obj.getData().getV_ac());
            state2JSON.setIAc(obj.getData().getI_ac());
            state2JSON.setTemperature(obj.getData().getTemperature());
            state2JSON.setVDCBoard(obj.getData().getVdcboard());
            state2JSON.setBrightness(obj.getData().getBrightness());
            state2JSON.setState(obj.getData().getState());
            result.add(state2JSON);

        }
        return result;
    }

    public static UriComponentsBuilder getRequestBody(Lamp lamp, String url) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        builder.queryParam("token", "1c68a488ec0d4dde80439e9627d23154");
        builder.queryParam("deviceEui", lamp.getName().toLowerCase());
        builder.queryParam("count", 10);
        builder.queryParam("offset", 0);
        builder.queryParam("startDate", simpleDateFormat.format(lamp.getLastUpdate()));
        builder.queryParam("endDate", simpleDateFormat.format(new Date()));
        builder.queryParam("order", "desc");
        return builder;
    }

}
