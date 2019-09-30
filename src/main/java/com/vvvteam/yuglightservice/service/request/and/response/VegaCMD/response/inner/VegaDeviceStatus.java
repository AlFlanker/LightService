package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class VegaDeviceStatus {
    private long ts;
    private String gatewayId;
    private boolean ack;
    private int fcnt;
    private int port;
    private String data;
    private int freq;
    private String dr;
    private int rssi;
    private float snr;
    private String type;
    private String packetStatus;

}
