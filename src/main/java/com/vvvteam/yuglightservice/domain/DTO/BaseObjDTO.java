package com.vvvteam.yuglightservice.domain.DTO;

import com.vvvteam.yuglightservice.domain.DTO.base.AdminDTOinfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BaseObjDTO extends AdminDTOinfo {
    private long id;
    private String name;
    private String alias;
    private String group;
    private Double latitude;
    private Double longitude;

    public BaseObjDTO(long id, String name, String alias, String group, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.group = group;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

