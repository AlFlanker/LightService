package com.vvvTeam.yuglightservice.service;

import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.entries.*;
import com.vvvTeam.yuglightservice.domain.support.LampByEui;
import com.vvvTeam.yuglightservice.domain.support.LampsLastStates;
import com.vvvTeam.yuglightservice.exceptions.VegaRxDataException;
import com.vvvTeam.yuglightservice.repositories.ControlPointsRepo;
import com.vvvTeam.yuglightservice.repositories.LampRepository;
import com.vvvTeam.yuglightservice.repositories.StatesRepository;
import com.vvvTeam.yuglightservice.repositories.support.LampEuiRepo;
import com.vvvTeam.yuglightservice.repositories.support.LampsStatesRepo;
import com.vvvTeam.yuglightservice.service.handlers.UpdateEventHandler;
import com.vvvTeam.yuglightservice.service.interfaces.AnalizeDataService;
import com.vvvTeam.yuglightservice.service.interfaces.LampService;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.MapResponseGetAll;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.BaseObj4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.Groups4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.Organization4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.WorkGroup4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.response.VegaRxDataResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AnalizeDataServiceImpl implements AnalizeDataService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final LampRepository lampRepository;
    private final StatesRepository statesRepository;
    private final LampsStatesRepo lampsStatesRepo;
    private final LampEuiRepo lampEuiRepo;


    /**
     * Конвертер формата EUI Vega -> YugLightService и Net868
     */
    private UnaryOperator<String> convert = o -> {
        int length = o.length();
        StringBuilder sb = new StringBuilder();
        sb.append(o, 0, 2);
        for (int i = 2; i < length; i += 2) {
            sb.append("-").append(o, i, i + 2);
        }
        return sb.toString();
    };

    /**
     *
     * @param dataResp
     * Обработка пакетов данных от Vega Server и отправка сообщения в WsSender
     */
    @Async
    @Transactional(rollbackFor = VegaRxDataException.class)
    public void updateStates(VegaRxDataResp dataResp) {
        if (Objects.isNull(dataResp) ) return;
        if (StringUtils.isEmpty(dataResp.getDevEui())) return;
        if (dataResp.getDevEui().length() != 16) return;
        if (StringUtils.isEmpty(dataResp.getData()) || dataResp.getData().length() != 36) return;
        if (Objects.isNull(dataResp.getTs())) return;
        String eui = convert.apply(dataResp.getDevEui());
        Lamp lamp;
        // Поиск в Кеше, а затем в БД. Намерено не в Stream API стиле!
        LampByEui lampEui = lampEuiRepo.findById(eui).orElse(null);
        if(Objects.isNull(lampEui)){
            lamp = lampRepository.findByNameAndIsDeleted(eui, false);
        } else{
            lamp = lampEui.getLamp();
        }
        if (Objects.nonNull(lamp) && (!StringUtils.isEmpty(dataResp.getData()))) {
            RawData rawData = RawData.decodeByteArrayToDevice(dataResp.getData());
            lamp.setLastUpdate(dataResp.getTs());
            StateObj currentState = new StateObj();
            currentState.setAck(false);
            currentState.setPassed(false);
            currentState.setCurrentDate(dataResp.getTs());
            currentState.setLamp(lamp);
            currentState.setDeleted(false);
            currentState.setData(rawData);
            Optional<LampsLastStates> lampsStates = lampsStatesRepo.findById(lamp);
            List<StateObj> currentStates;
            if(lampsStates.isPresent()){
                currentStates = lampsStates.get().getStateObj();
                currentStates.add(currentState);
            }else{
                LampsLastStates lastStates = new LampsLastStates();
                lastStates.setLamp(lamp);
                lastStates.getStateObj().add(currentState);
                lampsStatesRepo.save(lastStates);
            }
            UpdateEventHandler event = new UpdateEventHandler(this);
            event.setWorkGroup(lamp.getWorkGroup());
            event.setResponse(getLampWithLastStates(lamp, currentState));
            applicationEventPublisher.publishEvent(event);

        }
    }




    /**
     *
     * Обработка пакетов данных от Net868 и отправка сообщения в WsSender
     * ToDo: рефакторинг!! Т.к код уже древний
     */
    @Transactional(rollbackFor = {IllegalArgumentException.class, ParseException.class})
    public int addNewDataNet868(List<DataFromNet868> list, Organization organization) throws IllegalArgumentException {
        List<DataFromNet868> currentJson;
        if (Objects.isNull(list)) throw new IllegalArgumentException("List<DataFromNet868> cannot be null");
        if (Objects.isNull(organization)) throw new IllegalArgumentException("organization cannot be null");
        List<Lamp> existingObj = lampRepository.findByOrganizationOwnerAndIsDeleted(organization, false);
        WeakHashMap<Lamp, StateObj> updatedLamp = new WeakHashMap<>();
        MapResponseGetAll<Lamp> response;
        List<String> unuqEUI = list.stream().
                map(DataFromNet868::getDeviceEui)
                .distinct()
                .collect(Collectors.toList());

        for (String s : unuqEUI) {
            Lamp orgLamps = existingObj.stream()
                    .filter(a -> a.getName().toLowerCase().equals(s))
                    .findFirst().orElse(null);
            if (Objects.isNull(orgLamps)) return 0;
            if (orgLamps.getObjStates().size() == 0) {
                currentJson = getLastDataFromNet868(list, s);
                RawData d = RawData.decodeByteArrayToDevice(currentJson.get(0).getData());
                Location loc = new Location();
                loc.setLatitude((double) d.getLatitude());
                loc.setLongitude((double) d.getLongitude());
                loc.setAddress("");
                orgLamps.setLocation(loc);
                orgLamps.setLastUpdate(currentJson.get(0).getTime());
                lampRepository.save(orgLamps);
                List<StateObj> states = getAllStates(orgLamps, currentJson);
                if (states.size() > 0) {
                    states.sort(Comparator.comparing(StateObj::getCurrentDate).reversed());
                    updatedLamp.put(orgLamps, states.get(0));
                }
                statesRepository.saveAll(states);


            } else {
                currentJson = getLastDataFromNet868(list, s);
                RawData d = RawData.decodeByteArrayToDevice(currentJson.get(0).getData());
                Location loc = new Location();
                loc.setLatitude((double) d.getLatitude());
                loc.setLongitude((double) d.getLongitude());
                loc.setAddress("");
                orgLamps.setLocation(loc);
                lampRepository.save(orgLamps);
                updatedLamp.put(orgLamps, UpdateCurrentState(currentJson, orgLamps));
            }
        }
        for (Map.Entry<Lamp, StateObj> entry : updatedLamp.entrySet()) {
            UpdateEventHandler event = new UpdateEventHandler(this);
            event.setWorkGroup(entry.getKey().getWorkGroup());
            event.setResponse(getObj4MapWithState(entry.getKey(), entry.getValue()));
            applicationEventPublisher.publishEvent(event);
        }
        return 0;
    }

    private List<StateObj> getAllStates(Lamp bo, List<DataFromNet868> rawData) {
        List<StateObj> states = new ArrayList<>();
        StateObj stateObj;
        for (DataFromNet868 data : rawData) {
            stateObj = getStateObj(bo, data);
            stateObj.setAck(false);
            stateObj.setPassed(false);
            states.add(stateObj);
        }
        return states;
    }

    private List<DataFromNet868> getLastDataFromNet868(List<DataFromNet868> list, String s) {
        List<DataFromNet868> currentJson;
        currentJson = list.parallelStream().
                filter(x -> x.getDeviceEui().
                        equals(s)).
                sorted((a, b) -> {
                    if (a.getTime().after(b.getTime())) {
                        return -1;
                    } else if (a.getTime().before(b.getTime())) {
                        return 1;
                    } else return 0;
                }).
                collect(Collectors.toList());
        return currentJson;
    }

    private void saveCurrentState(List<DataFromNet868> currentJson, Lamp lamp) {
        StateObj state;
        for (DataFromNet868 data : currentJson) {
            stateCreate(lamp, data);
        }

    }

    private StateObj UpdateCurrentState(List<DataFromNet868> currentJson, Lamp lamp) {
        List<StateObj> stages = currentJson.stream()
                .filter(elem -> elem.getTime().after(lamp.getLastUpdate()))
                .map((DataFromNet868 element) -> {
                    return getStateObj(lamp, element);
                })
                .collect(Collectors.toList());

        statesRepository.saveAll(stages);
        lampRepository.save(lamp);
        if (stages.size() > 0) {
            stages.sort(Comparator.comparing(StateObj::getCurrentDate));
            return stages.get(0);
        } else
            return null;
    }

    private void stateCreate(Lamp lamp, DataFromNet868 data) {
        StateObj state = getStateObj(lamp, data);
        if (lamp.getLastUpdate().before(state.getCurrentDate())) {
            lamp.setLastUpdate(state.getCurrentDate());
            statesRepository.save(state);
        }
        //Update last date_of_changed field;
        lampRepository.save(lamp);
    }

    private StateObj getStateObj(Lamp lamp, DataFromNet868 data) {
        StateObj state;
        state = new StateObj();
        state.setData(RawData.decodeByteArrayToDevice(data.getData()));
        state.setCurrentDate(data.getTime());
        state.setFlags((byte) 0x00);
        state.setType("");
        state.setLamp(lamp);
        state.setAck(false);
        state.setDeleted(false);
        state.setPassed(false);
        return state;
    }

    /**
     *
     * @param lamp
     * @param state
     * @return Получение объекта DTO с его последнис состоянием для отображения на карте
     */
    @Transactional(readOnly = true)
    public BaseObj4Map getObj4MapWithState(Lamp lamp, StateObj state) {
        ControlPoint cp_owner = lamp.getCp_owner();
        Group group = lamp.getGroup();
        String gr_name = "";
        long cp_id = 0;
        if (Objects.nonNull(cp_owner) ) {
            cp_id = cp_owner.getId();
        }
        if (Objects.nonNull(group)) {
            gr_name = group.getNameOfGroup();
        }
        WorkGroup4Map wg4m = new WorkGroup4Map();
        wg4m.setId(lamp.getWorkGroup().getId());
        wg4m.setName(lamp.getWorkGroup().getName());
        Organization4Map o4m = new Organization4Map();
        o4m.setId(lamp.getOrganizationOwner().getId());
        o4m.setName(lamp.getOrganizationOwner().getName());
        BaseObj4Map obj4Map = new BaseObj4Map(lamp.getName(), lamp.getAlias(), ObjectsType.LAMP, lamp.getLocation(),
                Collections.singletonList(state), cp_id, lamp.getLastUpdate(), gr_name);
        obj4Map.setWorkGroup(wg4m);
        obj4Map.setOrganization(o4m);
        return obj4Map;
    }

    private BaseObj4Map getLampWithLastStates(Lamp lamp, StateObj lastState) {
        BaseObj4Map baseObj4Map = convert2Obj4Map(lamp);
        baseObj4Map.setObjStates(Collections.singletonList(lastState));
        return baseObj4Map;
    }

    /**
     * Конвертер Lamp -> DTO
     * @param lamp
     * @return
     */
    private BaseObj4Map convert2Obj4Map(Lamp lamp) {
        BaseObj4Map lampDto;
        lampDto = new BaseObj4Map();
        lampDto.setLastUpdate(lamp.getLastUpdate());
        lampDto.setAlias(lamp.getAlias());
        lampDto.setGroup(Objects.nonNull(lamp.getGroup()) ? lamp.getGroup().getNameOfGroup() : "");
        lampDto.setName(lamp.getName());
        lampDto.setLocation(lamp.getLocation());
        WorkGroup4Map wg4m = new WorkGroup4Map();
        wg4m.setId(lamp.getWorkGroup().getId());
        wg4m.setName(lamp.getWorkGroup().getName());
        Groups4Map groups4Map = new Groups4Map();
        if (Objects.nonNull(lamp.getGroup())) {
            groups4Map.setId(lamp.getGroup().getId());
            groups4Map.setName(lamp.getGroup().getNameOfGroup());
            wg4m.setGroups(Collections.singleton(groups4Map));
        }
        lampDto.setWorkGroup(wg4m);
        Organization4Map o4m = new Organization4Map();
        o4m.setId(lamp.getOrganizationOwner().getId());
        o4m.setName(lamp.getOrganizationOwner().getName());
        lampDto.setOrganization(o4m);
        if (Objects.nonNull(lamp.getCp_owner())) {
            lampDto.setCp_owner(lamp.getCp_owner().getId());
        }
        lampDto.setObjStates(Collections.emptyList());
        return lampDto;
    }

}
