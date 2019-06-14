package com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.requests.VegaGetDevices;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.response.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Getter
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "cmd"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VegaAuthResp.class, name = "auth_resp"),
        @JsonSubTypes.Type(value = VegaAllDeviceAppDataResp.class, name = "get_device_appdata_resp"),
        @JsonSubTypes.Type(value = VegaGetDataResp.class, name = "get_data_resp"),
        @JsonSubTypes.Type(value = VegaConsole.class, name = "console"),
        @JsonSubTypes.Type(value = VegaRxDataResp.class, name = "rx"),
        @JsonSubTypes.Type(value = VegaPong.class, name = "ping_resp"),
        @JsonSubTypes.Type(value = VegaDataResp.class, name = "send_data_resp"),
        @JsonSubTypes.Type(value = VegaGetDevicesResp.class, name = "get_devices_resp")


})
@NoArgsConstructor
public class VegaResp implements VegaExecutable{
    @Override
    public void execute(WebSocketSession session) {

    }

    @Override
    public void execute() {

    }
}
