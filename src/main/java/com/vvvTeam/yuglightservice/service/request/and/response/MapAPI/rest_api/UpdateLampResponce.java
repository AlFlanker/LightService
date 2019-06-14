package com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.rest_api;

import com.vvvTeam.yuglightservice.service.request.and.response.Responses.BaseObj4Map;
import lombok.Data;
import javax.validation.constraints.NotEmpty;

@Data
public class UpdateLampResponce {
    private String new_eui;
    private String old_eui;
    private BaseObj4Map data;
}
