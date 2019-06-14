package com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.response.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class DeviceVega {
    private String devEui;
    private String appEui;
    private String devName;
    private String adress1;
    private String devType;
    private String name;

}
