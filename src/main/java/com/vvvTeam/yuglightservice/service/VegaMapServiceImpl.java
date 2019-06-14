package com.vvvTeam.yuglightservice.service;

import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import com.vvvTeam.yuglightservice.domain.entries.Location;
import com.vvvTeam.yuglightservice.repositories.customsInterface.LampCustomRepository;
import com.vvvTeam.yuglightservice.service.interfaces.VegaMapService;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.response.VegaGetDevicesResp;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.response.inner.VegaDevicesRegInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VegaMapServiceImpl implements VegaMapService {
    private final LampCustomRepository lampCustomRepository;

    private final UnaryOperator<String> convert = o -> {
        int length = o.length();
        StringBuilder sb = new StringBuilder();
        sb.append(o, 0, 2);
        for (int i = 2; i < length; i += 2) {
            sb.append("-").append(o, i, i + 2);
        }
        return sb.toString();
    };

    /**
     * Выгрузка объектов из Vega сервера.
     * @param devicesResp
     * @param organization
     * @return List<Lamp> - лампы из сервиса у которых есть геолокация
     */
    @Async("taskExecutor")
    @Transactional
    @Override
    public CompletableFuture<List<Lamp>> addObjectsFromMap(VegaGetDevicesResp devicesResp, Organization organization) {
        if ("true".equals(devicesResp.getStatus())) {
            List<Lamp> lamps = new ArrayList<>();
            Set<String> names = devicesResp.getDevices_list().stream()
                    .filter(device -> device.getPosition().getLongitude() > 0.0 && device.getPosition().getLatitude() > 0.0)
                    .map(vegaDevicesRegInfo -> convert.apply(vegaDevicesRegInfo.getDevEui()))
                    .collect(Collectors.toSet());
            List<String> existLamp = lampCustomRepository.getExistLamp(names, false);
            if (existLamp.size() == names.size()) return CompletableFuture.completedFuture(Collections.emptyList());
            else {
                names.removeAll(existLamp);
                Set<VegaDevicesRegInfo> collect = devicesResp.getDevices_list().stream().filter(vegaDevicesRegInfo -> names.contains(convert.apply(vegaDevicesRegInfo.getDevEui())))
                        .collect(Collectors.toSet());
                collect.forEach(device -> {
                    Lamp lampFromVega = new Lamp();
                    Location location = new Location();
                    location.setLatitude(device.getPosition().getLatitude());
                    location.setLongitude(device.getPosition().getLongitude());
                    lampFromVega.setLocation(location);
                    lampFromVega.setName(device.getDevEui());
                    lampFromVega.setAlias(device.getDevName());
                    lampFromVega.setCp_owner(null);
                    lampFromVega.setGroup(null);
                    lampFromVega.setOrganizationOwner(organization);
                    lamps.add(lampFromVega);
                });
                return CompletableFuture.completedFuture(lamps);
            }

        }
        else return CompletableFuture.completedFuture(Collections.emptyList());
    }

}
