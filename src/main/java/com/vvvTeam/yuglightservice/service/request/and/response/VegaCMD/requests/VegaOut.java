package com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.requests;

import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.VegaRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class VegaOut extends VegaRequest {
    private String token;
}
