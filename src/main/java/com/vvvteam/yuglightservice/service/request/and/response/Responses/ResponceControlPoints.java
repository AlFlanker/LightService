package com.vvvteam.yuglightservice.service.request.and.response.Responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;
@Data
@NoArgsConstructor
public class ResponceControlPoints {
    @NonNull
    private String msg;
    private List<ResponceControlPoint> controlPointList;
}
