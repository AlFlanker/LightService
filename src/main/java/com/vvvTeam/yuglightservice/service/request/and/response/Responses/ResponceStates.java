package com.vvvTeam.yuglightservice.service.request.and.response.Responses;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
public class ResponceStates {
    public String msg;
    public List<State2JSON> data;
}
