package com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.requests;

import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.VegaRequest;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.requests.inner.VegaDReqBody;
import lombok.*;

import java.util.List;

/**
 * Отправка команд Vega серверу
 */
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Getter @Setter
/**
 * @see VegaRequest
 */
public class VegaDataRequest extends VegaRequest {
    private List<VegaDReqBody> data_list;
}
