package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.VegaResp;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.response.inner.AppendStatus;
import lombok.*;

import java.util.List;

/**
 * Прием подтверждени команды от Vega Сервера
 */
@Data
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VegaDataResp extends VegaResp {
    private boolean status;
    private String err_string;
    private List<AppendStatus> append_status;

}
