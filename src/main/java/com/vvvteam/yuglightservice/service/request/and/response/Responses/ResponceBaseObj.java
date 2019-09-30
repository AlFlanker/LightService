package com.vvvteam.yuglightservice.service.request.and.response.Responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponceBaseObj {
    private String alias;
    private String name;
    private Double latitude;
    private Double longitude;
    private String objectsType;
}
