package com.vvvTeam.yuglightservice.service.request.and.response.MapAPI;

import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapType;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import com.vvvTeam.yuglightservice.domain.auth.User;

public interface MapExecutable {
    MapResponse execute(User user, MapType objectType);
}
