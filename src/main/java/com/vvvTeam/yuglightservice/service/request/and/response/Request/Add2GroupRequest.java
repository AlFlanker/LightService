package com.vvvTeam.yuglightservice.service.request.and.response.Request;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Add2GroupRequest {
    private String eui;
    private String groupsName;
}
