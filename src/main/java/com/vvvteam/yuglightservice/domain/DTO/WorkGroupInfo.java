package com.vvvteam.yuglightservice.domain.DTO;

import com.vvvteam.yuglightservice.domain.DTO.base.AdminDTOinfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class WorkGroupInfo extends AdminDTOinfo {
    private long id;
    private String name;
    private String wgFounder;
    private long userCount;
    private long lampCount;
    private long cpCount;
}
