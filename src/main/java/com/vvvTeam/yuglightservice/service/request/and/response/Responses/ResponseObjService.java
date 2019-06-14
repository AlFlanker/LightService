package com.vvvTeam.yuglightservice.service.request.and.response.Responses;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
public class ResponseObjService {
    private String msg;
    private List<BaseObj4Map> objectList;
}
