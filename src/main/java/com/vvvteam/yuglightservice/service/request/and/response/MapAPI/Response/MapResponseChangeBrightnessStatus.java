package com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response;

import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.Support.DeviceChangeBriStatus;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapObjectType;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class MapResponseChangeBrightnessStatus extends MapResponse {
    @Setter @Getter
    private MapObjectType type;
    @Setter @Getter
    private List<DeviceChangeBriStatus> payload;
}
