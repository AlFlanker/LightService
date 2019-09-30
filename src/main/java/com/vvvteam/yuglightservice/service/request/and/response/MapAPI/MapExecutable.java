package com.vvvteam.yuglightservice.service.request.and.response.MapAPI;

import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapType;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import com.vvvteam.yuglightservice.domain.auth.User;

public interface MapExecutable {
    MapResponse execute(User user, MapType objectType);
}
