package com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.Support;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class PassedObj {
    private String name;
    private List<Long> passedID;

}
