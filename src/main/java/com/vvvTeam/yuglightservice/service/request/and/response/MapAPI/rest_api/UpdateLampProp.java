package com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.rest_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateLampProp {

    private String new_eui;
    @NotEmpty(message = "не может быть пустым")
    private String eui;
    @Length(min = 5,max = 60)
    private String alias;
    private Long group_id;
    private Double lat;
    private Double lon;
}
