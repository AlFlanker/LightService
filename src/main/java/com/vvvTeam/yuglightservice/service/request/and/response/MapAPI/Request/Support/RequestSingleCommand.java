package com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.Support;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
public class RequestSingleCommand {
    private String eui;
    private String command;
}
