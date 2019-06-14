package com.vvvTeam.yuglightservice.domain.support;

import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;
@Data
@AllArgsConstructor
@KeySpace("lampsEUI")
public class LampByEui {
    @Id
    private String eui;
    private Lamp lamp;
}
