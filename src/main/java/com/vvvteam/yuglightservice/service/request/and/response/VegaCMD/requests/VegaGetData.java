package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.requests;

import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.VegaRequest;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.requests.inner.VegaSelect;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class VegaGetData extends VegaRequest {
    private String devEui;
    private VegaSelect select;



}
