package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.VegaResp;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response.inner.VegaDeviceStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class VegaGetDataResp extends VegaResp {
    private String status;
    private String err_string;
    private String devEui;
    private String appEui;
    private String direction;
    private int totalNum;
    private List<VegaDeviceStatus> data_list;

}
