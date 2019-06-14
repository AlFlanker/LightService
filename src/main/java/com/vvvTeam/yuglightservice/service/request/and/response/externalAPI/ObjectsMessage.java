package com.vvvTeam.yuglightservice.service.request.and.response.externalAPI;

import com.vvvTeam.yuglightservice.service.request.and.response.Request.LampPropsMsg;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;
@NoArgsConstructor
@Getter @Setter
public class ObjectsMessage {
    @NotBlank(message ="cannot be null!")
    public String token;
    public List<LampPropsMsg> objMessage;
}
