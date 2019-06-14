package com.vvvTeam.yuglightservice.domain.DTO;

import com.vvvTeam.yuglightservice.domain.DTO.base.AdminDTOinfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OrganizationInfo extends AdminDTOinfo {
    private long id;
    private String name;
    private String founderUser;
    private int workGroupCount;
    private int usersCount;
    private int lampCount;
    private int cpCount;
}
