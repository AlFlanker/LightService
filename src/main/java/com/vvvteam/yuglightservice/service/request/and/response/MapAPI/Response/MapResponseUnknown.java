package com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response;

import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapObjectType;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class MapResponseUnknown extends MapResponse {
    @Setter @Getter
    private MapObjectType type;

}
