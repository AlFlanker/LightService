package com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.VegaResp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VegaConsole extends VegaResp {
    private String common;
}
