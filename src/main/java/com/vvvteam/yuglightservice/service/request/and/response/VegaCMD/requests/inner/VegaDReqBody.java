package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.requests.inner;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class VegaDReqBody {
    private String devEui;
    private String data;
    private short port;
    public void setPort(short port) {
        if(port <1 || port >223) {
            this.port = 3;
        }
        else this.port = port;
    }
}
