package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class VegaDevicesRegInfo {
    private String devEui;
    private String devName;
    private Abp abp;
    private OTAA otaa;
    private Position position;


}
