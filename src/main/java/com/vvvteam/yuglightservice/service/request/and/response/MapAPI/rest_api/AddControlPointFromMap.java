package com.vvvteam.yuglightservice.service.request.and.response.MapAPI.rest_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddControlPointFromMap {
    @NotEmpty(message = "не может быть пустым")
    private String name;
    private Double lat;
    private Double lon;
    private Long organization;
    private Long workGroup;
}
