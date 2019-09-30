package com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.Support;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DeviceChangeBriStatus {
    private String eui;
    private String status;
}
