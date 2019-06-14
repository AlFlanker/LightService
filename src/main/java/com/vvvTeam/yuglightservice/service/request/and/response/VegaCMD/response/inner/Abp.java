package com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.response.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class Abp {
    private String devAddress;
    private String appsKey;
    private String nwksKey;
}
