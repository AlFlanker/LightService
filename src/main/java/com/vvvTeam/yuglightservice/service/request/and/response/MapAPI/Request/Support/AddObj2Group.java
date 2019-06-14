package com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.Request.Support;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class AddObj2Group {
    private List<String> eui;
    private String groupsName;
}
