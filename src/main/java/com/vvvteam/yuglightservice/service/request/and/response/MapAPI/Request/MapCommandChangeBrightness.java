package com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.vvvteam.yuglightservice.domain.auth.NetData;
import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.domain.validators.Command;
import com.vvvteam.yuglightservice.repositories.LampRepository;
import com.vvvteam.yuglightservice.repositories.NetDataRepo;
import com.vvvteam.yuglightservice.service.interfaces.NetDataService;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.MapExecutable;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.Support.RequestSingleCommand;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapRequest;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapType;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.MapResponseChangeBrightnessStatus;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.Support.DeviceChangeBriStatus;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapObjectType;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapStatus;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.requests.VegaDataRequest;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.requests.inner.VegaDReqBody;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Смена яркость
 * не дает инициализировать филды в конструкторе
 */
@Slf4j
@Scope("prototype")
@Component
@NoArgsConstructor
@AllArgsConstructor
public class MapCommandChangeBrightness extends MapRequest implements MapExecutable {
    @JsonIgnore
    private NetDataRepo netDataRepo;
    @JsonIgnore
    private NetDataService netDataService;
    @JsonIgnore
    private LampRepository lampRepository;
    @JsonIgnore
    private GenericWebApplicationContext context;

    @Autowired
    public void setNetDataRepo(NetDataRepo netDataRepo) {
        this.netDataRepo = netDataRepo;
    }

    @Autowired
    public void setNetDataService(NetDataService netDataService) {
        this.netDataService = netDataService;
    }

    @Autowired
    public void setLampRepository(LampRepository lampRepository) {
        this.lampRepository = lampRepository;
    }

    @Autowired
    public void setContext(GenericWebApplicationContext context) {
        this.context = context;
    }

    @Getter
    @Setter
    private RequestSingleCommand payload;

    @Override
    public MapResponse execute(User user, MapType type) {
        return change(user);

    }

    protected MapResponseChangeBrightnessStatus change(User user)  {
        ObjectMapper mapper = new ObjectMapper();
        List<NetData> netData = netDataRepo.findByOwner(user.getOrganizationOwner());
        DeviceChangeBriStatus briStatus = new DeviceChangeBriStatus();
        MapResponseChangeBrightnessStatus response = new MapResponseChangeBrightnessStatus();
        response.setType(MapObjectType.changeBrightness);
        Optional<NetData> current = netData.stream()
                .filter(NetData::isActive)
                .findFirst();
        if (current.isPresent()) {
            NetData net_data = current.get();
            switch (net_data.getTypeOfService()) {
                case Vega:
                    VegaDataRequest request = new VegaDataRequest();
                    VegaDReqBody body = new VegaDReqBody();
                    body.setData(Integer.toHexString(Integer.parseInt(payload.getCommand())));
                    body.setDevEui(payload.getEui().replace("-",""));
                    body.setPort((short) 3);
                    List<VegaDReqBody> bodyList = new ArrayList<>();
                    bodyList.add(body);
                    request.setData_list(bodyList);
                    try {
                        String res = mapper.writeValueAsString(request);
                        netDataService.SendCommandVega(user, request);
                    }
                    catch (JsonProcessingException e){
                        e.getMessage();
                    }
                    log.info("Send command via Vega");
                    break;
                case Net868:
                    log.info("Send command via Net868");
                    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(net_data.getAddress2());
                    builder.queryParam("token", net_data.getToken());
                    ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
                    Command command = new Command();
                    command.setCommandTypeId(payload.getCommand());
                    command.setDeviceEui(payload.getEui().toLowerCase());
                    try {
                        String json = objectWriter.writeValueAsString(command);
                        String statusCode = netDataService.sendCommand(builder, json).get();
                        briStatus.setEui(payload.getEui());
                        briStatus.setStatus(statusCode);
                        response.setPayload(Arrays.asList(briStatus));
                        response.setStatus(MapStatus.success);
                        return response;
                    } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    briStatus.setEui(payload.getEui());
                    briStatus.setStatus("fail");
                    response.setStatus(MapStatus.bad_data);
                    response.setPayload(Arrays.asList(briStatus));
                    break;
            }
        }
        return null;
    }


}
