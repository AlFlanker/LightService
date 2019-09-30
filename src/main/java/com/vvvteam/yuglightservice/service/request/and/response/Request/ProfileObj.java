package com.vvvteam.yuglightservice.service.request.and.response.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vvvteam.yuglightservice.domain.validators.Annotations.ValidNetData;
import lombok.Data;
import lombok.NoArgsConstructor;

@ValidNetData.List({
        @ValidNetData(
                field = "service",
                max = 15,
                min =3,
                message = "Service do not match!")
})
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class ProfileObj {
    private String service;
    private String net868_token;
    private String vega_username;
    private String vega_password;
    private String isActive;
    @JsonProperty("url")
    private String address;
    @JsonProperty("url2")
    private String address2;
    @JsonProperty("url3")
    private String wss;
    private Long id;
}
