package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response.inner;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppendStatus {
    private String devEui;
    private boolean status;
}
