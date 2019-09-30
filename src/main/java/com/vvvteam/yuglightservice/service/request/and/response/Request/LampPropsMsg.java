package com.vvvteam.yuglightservice.service.request.and.response.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LampPropsMsg {
    @Size(max = 60)
    private String alias;
    @NotBlank(message = "alias can't be empty")
    private String eui;
    @NotBlank(message = "EUI can't be empty")
    private String type;
    @NotBlank(message = "can't be empty")
    private String latitude;
    @NotBlank(message = "can't be empty")
    private String longitude;
    private String cp;
    private String groupsName;

}
