package com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.requests.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VegaSelect {
    private int limit;
}
