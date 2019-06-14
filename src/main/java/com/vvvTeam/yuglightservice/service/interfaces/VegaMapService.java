package com.vvvTeam.yuglightservice.service.interfaces;

import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.response.VegaGetDevicesResp;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface VegaMapService {
    CompletableFuture<List<Lamp>> addObjectsFromMap(VegaGetDevicesResp devicesResp, Organization organization);
}
