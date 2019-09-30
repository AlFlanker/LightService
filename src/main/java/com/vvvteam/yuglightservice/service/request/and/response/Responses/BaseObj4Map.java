package com.vvvteam.yuglightservice.service.request.and.response.Responses;

import com.vvvteam.yuglightservice.domain.entries.Location;
import com.vvvteam.yuglightservice.domain.entries.ObjectsType;
import com.vvvteam.yuglightservice.domain.entries.StateObj;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"name","objectsType","location","lastUpdate"})
@ToString(of = {"name","alias","lastUpdate","group"})
public class BaseObj4Map {
    private String name;
    private String alias;
    private ObjectsType objectsType;
    private Location location;
    private List<StateObj> objStates = new ArrayList<>();
    private long cp_owner;
    private Date lastUpdate;
    private String group;
    private WorkGroup4Map workGroup;
    private Organization4Map organization;

    public BaseObj4Map(String name, String alias, ObjectsType objectsType, Location location, List<StateObj> objStates, long cp_owner, Date lastUpdate, String group) {
        this.name = name;
        this.alias = alias;
        this.objectsType = objectsType;
        this.location = location;
        this.objStates = objStates;
        this.cp_owner = cp_owner;
        this.lastUpdate = lastUpdate;
        this.group = group;
    }
}
