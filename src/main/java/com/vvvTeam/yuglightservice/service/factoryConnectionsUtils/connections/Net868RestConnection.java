package com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.connections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vvvTeam.yuglightservice.domain.auth.NetData;
import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.entries.DataFromNet868;
import com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.interfaces.KindOfConnection.RestConnect;
import com.vvvTeam.yuglightservice.service.interfaces.AnalizeDataService;
import com.vvvTeam.yuglightservice.service.interfaces.NetDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

//ToDO: запросы по времени. Start / Stop в теле запроса!
@Slf4j
@Scope("prototype")
@Component
public class Net868RestConnection implements RestConnect {
    private final AnalizeDataService analizeDataService;
    private final NetDataService netDataService;
    private NetData netData;

    @Autowired
    public Net868RestConnection(AnalizeDataService analizeDataService, NetDataService netDataService) {
        this.analizeDataService = analizeDataService;
        this.netDataService = netDataService;

    }

    @Override
    public void connect() throws IllegalArgumentException {
        if (Objects.isNull(netData.getOwner())) throw new IllegalArgumentException("organization cannot be null!");
        if (Objects.isNull(netData)) throw new IllegalArgumentException("netData cannot be null!");
        try {
            netDataService.findData(getRequestBody(netData))
                    .thenAccept(res -> hideAnalize(netData.getOwner(), res));
        } catch (ParseException | InterruptedException e) {
            log.warn(e.getMessage());
        }

    }


    public void setNetData(NetData netData) {
        this.netData = netData;
    }


    private void hideAnalize(Organization organization, String res) {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<DataFromNet868> jsonData = new ArrayList<>();
        log.info("start hideAnalize");
        try {
            jsonData.addAll(objectMapper.
                    readValue(res, typeFactory.
                            constructCollectionType(List.class, DataFromNet868.class)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("hideAnalize result ->\n " + jsonData.toString());

        analizeDataService.addNewDataNet868(jsonData, organization);

    }

    private UriComponentsBuilder getRequestBody(NetData netData) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -3);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(netData.getAddress());
        builder.queryParam("token", netData.getToken());
        builder.queryParam("count", 100);
        builder.queryParam("offset", 0);
        builder.queryParam("order", "desc");
        return builder;
    }



}
