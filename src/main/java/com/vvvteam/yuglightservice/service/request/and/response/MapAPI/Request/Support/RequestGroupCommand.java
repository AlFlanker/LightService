package com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.Support;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestGroupCommand {
    private String group;
    private List<String> lamps;
    private String command;

}
