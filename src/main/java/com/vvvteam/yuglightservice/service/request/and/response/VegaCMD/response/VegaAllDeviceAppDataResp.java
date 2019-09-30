package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.VegaResp;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.requests.VegaGetData;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.requests.inner.VegaSelect;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response.inner.DeviceVega;
import com.vvvteam.yuglightservice.domain.auth.Vega.UserSessionData;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class VegaAllDeviceAppDataResp extends VegaResp {
    private String status;
    private String err_string;
    private List<DeviceVega> devices_list;
    @Getter
    @Setter
    @JsonIgnore
    private Long organizationId;
    @JsonIgnore
    @Autowired
    private UserSessionData userSessionData;

    @Override
    public void execute(WebSocketSession session) {
        ObjectMapper objectMapper = new ObjectMapper();
        for (DeviceVega device : devices_list) {
            VegaGetData vegaGetData = new VegaGetData();
            vegaGetData.setDevEui(device.getDevEui());
            VegaSelect vegaSelect = new VegaSelect();
            vegaSelect.setLimit(10);
            vegaGetData.setSelect(vegaSelect);
            try {
                session = userSessionData.getSessions().get(organizationId);
                if (Objects.nonNull(session)) {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(vegaGetData)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
