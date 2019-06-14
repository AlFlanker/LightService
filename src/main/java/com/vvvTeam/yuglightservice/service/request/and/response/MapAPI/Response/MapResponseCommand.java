package com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response;

import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapObjectType;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class MapResponseCommand extends MapResponse {
    @Setter @Getter
    private MapObjectType type;
}
