package com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response;

import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapObjectType;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter @Getter
public class MapResponseAddMapGroup extends MapResponse {
    private MapObjectType type;
    private String groupsName;
    private Long id;
}
