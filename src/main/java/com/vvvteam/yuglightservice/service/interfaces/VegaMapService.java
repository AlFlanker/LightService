package com.vvvteam.yuglightservice.service.interfaces;

import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response.VegaGetDevicesResp;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface VegaMapService {
    CompletableFuture<List<Lamp>> addObjectsFromMap(VegaGetDevicesResp devicesResp, Organization organization);
}
