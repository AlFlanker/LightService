package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.requests.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "cmd"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VegaAuth.class, name = "auth_req"),
        @JsonSubTypes.Type(value = VegaGetDevicesAppData.class, name = "get_device_appdata_req"),
        @JsonSubTypes.Type(value = VegaGetData.class, name = "get_data_req"),
        @JsonSubTypes.Type(value = VegaPing.class, name = "ping_req"),
        @JsonSubTypes.Type(value = VegaDataRequest.class, name = "send_data_req"),
        @JsonSubTypes.Type(value = VegaGetDevices.class,name = "get_devices_req")
})
@NoArgsConstructor
@Data
public class VegaRequest {

}
