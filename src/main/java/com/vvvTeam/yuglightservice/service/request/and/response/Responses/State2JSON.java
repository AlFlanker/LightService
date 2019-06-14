package com.vvvTeam.yuglightservice.service.request.and.response.Responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
@NoArgsConstructor
public class State2JSON {
    private int vAc;
    private int iAc;
    private int temperature;
    private int vDCBoard;
    private float latitude;
    private float longitude;
    private byte brightness;
    private byte state;
    private String objAlias;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date currentDate;
}
