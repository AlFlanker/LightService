package com.vvvTeam.yuglightservice.domain.entries;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataFromNet868 {


    private String id;
    private String deviceEui;
    private Date time;
    private String seqNo;
    private String port;
    private String data;


    private static Date fromISO8601UTC(String dateStr) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Date();
    }
}
