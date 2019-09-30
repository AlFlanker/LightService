package com.vvvteam.yuglightservice.service.request.and.response.MapAPI.rest_api;

import com.vvvteam.yuglightservice.service.request.and.response.Responses.BaseObj4Map;
import lombok.Data;

@Data
public class UpdateLampResponce {
    private String new_eui;
    private String old_eui;
    private BaseObj4Map data;
}
