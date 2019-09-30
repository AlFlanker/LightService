package com.vvvteam.yuglightservice.domain.support;

import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.entries.StateObj;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@KeySpace("lampsStates")
public class LampsLastStates {
    @Id
    private Lamp lamp;
    private List<StateObj> stateObj = Collections.synchronizedList(new ArrayList<>());
}
