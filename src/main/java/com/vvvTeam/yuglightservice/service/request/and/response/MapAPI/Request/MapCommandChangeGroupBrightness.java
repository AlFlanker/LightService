package com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.vvvTeam.yuglightservice.domain.auth.NetData;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.domain.entries.Group;
import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import com.vvvTeam.yuglightservice.domain.validators.Command;
import com.vvvTeam.yuglightservice.repositories.GroupRepository;
import com.vvvTeam.yuglightservice.repositories.LampRepository;
import com.vvvTeam.yuglightservice.repositories.NetDataRepo;
import com.vvvTeam.yuglightservice.service.interfaces.NetDataService;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.MapExecutable;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.Support.RequestGroupCommand;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.Support.RequestSingleCommand;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapRequest;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapType;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.MapResponseChangeBrightnessStatus;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.Support.DeviceChangeBriStatus;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapObjectType;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapStatus;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.requests.VegaDataRequest;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.requests.inner.VegaDReqBody;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Scope("prototype")
@Component
@RequiredArgsConstructor
public class MapCommandChangeGroupBrightness extends MapRequest implements MapExecutable {

    @JsonIgnore
    private GroupRepository groupRepository;
    @JsonIgnore
    private LampRepository lampRepository;
    @JsonIgnore
    private NetDataRepo netDataRepo;
    @JsonIgnore
    private NetDataService netDataService;

    @Autowired
    public void setNetDataService(NetDataService netDataService) {
        this.netDataService = netDataService;
    }

    @Autowired
    public void setNetDataRepo(NetDataRepo netDataRepo) {
        this.netDataRepo = netDataRepo;
    }

    @Autowired
    public void setLampRepository(LampRepository lampRepository) {
        this.lampRepository = lampRepository;
    }

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Getter
    @Setter
    private RequestGroupCommand payload;

    @Override
    public MapResponse execute(User user, MapType type) {
        return send(user);

    }

    private List<Lamp> getCurBaseObjectsGroup(long id) {
        Group group = groupRepository.findById(id).orElse(null);
        if (group != null) {
            return Optional.of(lampRepository.findByGroup(group))
                    .orElse(Collections.emptyList());
        }
        return Collections.emptyList();
    }

    private MapResponseChangeBrightnessStatus send(User user) {
        MapResponseChangeBrightnessStatus response = new MapResponseChangeBrightnessStatus();
        response.setType(MapObjectType.changeBrightness);
        List<Lamp> curBaseObjectsGroup;
        if(payload.getLamps().size()>1){
            curBaseObjectsGroup=lampRepository.findByNameIn(payload.getLamps()).orElse(Collections.emptyList());
        }else {
          curBaseObjectsGroup = getCurBaseObjectsGroup(Long.valueOf(payload.getGroup()));
        }
        RequestSingleCommand command;
        List<DeviceChangeBriStatus> list = new ArrayList<>();
        List<RequestSingleCommand> commands = new ArrayList<>();
        if (curBaseObjectsGroup.size() > 0) {
            for (Lamp b : curBaseObjectsGroup) {
                command = new RequestSingleCommand();
                command.setCommand(payload.getCommand());
                command.setEui(b.getName());
                commands.add(command);
            }
            changeGroupBrightness(user,commands);

            response.setPayload(list);
            response.setStatus(MapStatus.success);
            return response;
        }
        return null;
    }

    private void changeGroupBrightness(User user,List<RequestSingleCommand> commands){
        List<NetData> netData = netDataRepo.findByOwner(user.getOrganizationOwner());

        Optional<NetData> current = netData.stream()
                .filter(NetData::isActive)
                .findFirst();
        if (current.isPresent()) {
            NetData net_data = current.get();
            switch (net_data.getTypeOfService()) {
                case Vega:
                    VegaDataRequest request = new VegaDataRequest();
                    VegaDReqBody body;
                    List<VegaDReqBody> bodyList = new ArrayList<>();
                    for(RequestSingleCommand command:commands) {
                        body = new VegaDReqBody();
                        body.setData(Integer.toHexString(Integer.parseInt(payload.getCommand())));
                        body.setDevEui(command.getEui().replace("-", ""));
                        body.setPort((short) 3);
                        bodyList.add(body);
                    }
                    request.setData_list(bodyList);
                    netDataService.SendCommandVega(user, request);
                    log.info("Send command via Vega");
                    break;
                case Net868:
                    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(net_data.getAddress2());
                    builder.queryParam("token", net_data.getToken());
                    ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
                    Command com;
                    for(RequestSingleCommand command:commands) {
                        com=  new Command();
                        com.setCommandTypeId(command.getCommand());
                        com.setDeviceEui(command.getEui());
                        try {
                            String json = objectWriter.writeValueAsString(command);
                           netDataService.sendCommand(builder, json).get();
                        } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }
    private DeviceChangeBriStatus change(User user, RequestSingleCommand data) {
        List<NetData> netData = netDataRepo.findByOwner(user.getOrganizationOwner());
        DeviceChangeBriStatus briStatus = new DeviceChangeBriStatus();
        briStatus.setEui(data.getEui());
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
                    body.setDevEui(data.getEui().replace("-", ""));
                    body.setPort((short) 3);
                    List<VegaDReqBody> bodyList = new ArrayList<>();
                    bodyList.add(body);
                    request.setData_list(bodyList);
                    netDataService.SendCommandVega(user, request);
                    log.info("Send command via Vega");
                    break;
                case Net868:
                    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(net_data.getAddress2());
                    builder.queryParam("token", net_data.getToken());
                    ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
                    Command command = new Command();
                    command.setCommandTypeId(data.getCommand());
                    command.setDeviceEui(data.getEui());
                    try {
                        String json = objectWriter.writeValueAsString(command);
                        briStatus.setStatus(netDataService.sendCommand(builder, json).get());
                        return briStatus;
                    } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            briStatus.setStatus("fail");
            return briStatus;
        }
        return null;
    }
}