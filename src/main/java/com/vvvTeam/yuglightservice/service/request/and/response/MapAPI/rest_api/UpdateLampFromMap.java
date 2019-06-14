package com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.rest_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateLampFromMap {
    @NotEmpty(message = "не может быть пустым")
    private String eui;
    private String old_eui;
    @Length(min = 5,max = 20)
    private String alias;
    private Double lat;
    private Double lon;
    private Long cp;
    private Long organization;
    private Long workGroup;
    private Long group;

}
