package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class Position {
    private double longitude;
    private double latitude;
    private int altitude;


}
