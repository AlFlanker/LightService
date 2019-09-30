package com.vvvteam.yuglightservice.domain.entries.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class RawDataDto implements Serializable {
    private int v_ac;
    private int i_ac;
    private int temperature;
    private int vdcboard;
    private float latitude;
    private float longitude;
    private byte brightness;
    private byte state;
    private Date date_of_changed;

    public RawDataDto(int v_ac, int i_ac, int temperature, int vdcboard, float latitude, float longitude, byte brightness, byte state, Date date_of_changed) {
        this.v_ac = v_ac;
        this.i_ac = i_ac;
        this.temperature = temperature;
        this.vdcboard = vdcboard;
        this.latitude = latitude;
        this.longitude = longitude;
        this.brightness = brightness;
        this.state = state;
        this.date_of_changed = date_of_changed;
    }

    /**
     * с маленькой буквы для наглядности
     */
    public static enum Fields{
        v_ac,i_ac,temperature,vdcboard,latitude,longitude,brightness,state,date_of_changed
    }
}
