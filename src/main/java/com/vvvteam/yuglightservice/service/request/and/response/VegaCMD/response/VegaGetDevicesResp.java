package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.VegaResp;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response.inner.VegaDevicesRegInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class VegaGetDevicesResp extends VegaResp {
    private String status;
    private String err_string;
    private List<VegaDevicesRegInfo> devices_list;

}
