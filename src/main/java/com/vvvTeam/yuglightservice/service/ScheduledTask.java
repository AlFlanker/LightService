package com.vvvTeam.yuglightservice.service;

import com.vvvTeam.yuglightservice.domain.auth.NetData;
import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import com.vvvTeam.yuglightservice.domain.entries.StateObj;
import com.vvvTeam.yuglightservice.domain.support.LampsLastStates;
import com.vvvTeam.yuglightservice.repositories.LampRepository;
import com.vvvTeam.yuglightservice.repositories.OrganizationRepo;
import com.vvvTeam.yuglightservice.repositories.StatesRepository;
import com.vvvTeam.yuglightservice.repositories.support.LampsStatesRepo;
import com.vvvTeam.yuglightservice.service.factoryConnectionsUtils.interfaces.ConnectFactory;
import javafx.print.Collation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduledTask {
    private final OrganizationRepo organizationRepo;
    private final StatesRepository statesRepository;
    private final LampRepository lampRepository;
    private final ConnectFactory connectFactory;
    private final LampsStatesRepo lampsStatesRepo;

    /**Устанавливает соединения с выбранным источником данных Vega или Net868 для каждой активной организации.*/

    @Transactional
    @Scheduled(fixedRate = 100000)
    public void reportCurrentTime() {
        List<Organization> organizations = getOrganization();
        for (Organization organization : organizations) {
            List<NetData> netData = Optional.of(organization.getNetData())
                    .orElse(Collections.emptyList());
            Optional<NetData> current;
            current = netData.stream()
                    .filter(NetData::isActive)
                    .findFirst();
            if (current.isPresent()) {
                NetData net_data = current.get();
                try {
                    connectFactory.getWebSocketConnect(net_data).connect();
                }catch (IllegalArgumentException e){
                    log.error(net_data.getOwner().getName() + " net data contains incorrect data");
                }
            }

        }

    }

    /**
     * Сохранения всех новых состовний объектов в БД
     */
    /**
     * ToDo: удалять только сохраненные данные из KeyVal Кеша(т.е изменить логику clearCache())
     */
    @Transactional
    @Scheduled(fixedRate = 100000)
    public void saveLampsCacheInDb(){

        List<LampsLastStates> lampsLastStates = lampsStatesRepo.findAll();
        List<Lamp> lamps = lampsLastStates.stream().map(LampsLastStates::getLamp).collect(Collectors.toList());
        List<List<StateObj>> collect = lampsLastStates.stream().map(LampsLastStates::getStateObj).collect(Collectors.toList());
        log.info("Кол-во объектов " +lamps.size());
        log.info("Общее Кол-во состояний " +collect.stream().map(List::size).reduce(Integer::sum).orElse(0));
        List<StateObj> all = new ArrayList<>();

        List<Lamp> byNameIn = lampRepository.findByNameIn(lampsLastStates.stream()
                .map(LampsLastStates::getLamp)
                    .map(Lamp::getName)
                        .collect(Collectors.toList()))
                        .orElse(Collections.emptyList());

        for(LampsLastStates states:lampsLastStates){
            Lamp curLamp = byNameIn.stream().filter(lamp -> lamp.getName().equals(states.getLamp().getName())).findFirst().get();
                if(Objects.nonNull(curLamp)) {
                    List<StateObj> stateObj = states.getStateObj();

                    stateObj.forEach(s -> s.setLamp(curLamp));
                    all.addAll(stateObj);
                }
        }
        statesRepository.saveAll(all);
        clearCache();

    }

    private void clearCache(){
        lampsStatesRepo.deleteAll();
    }

    private List<Organization> getOrganization() {
        return organizationRepo.findAll();

    }
    //******************************


}
















