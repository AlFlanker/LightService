package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class OTAA {
    private String appEui;
    private String appKey;
    private int last_join_ts;
}
