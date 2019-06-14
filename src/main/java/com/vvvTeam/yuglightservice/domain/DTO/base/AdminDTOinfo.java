package com.vvvTeam.yuglightservice.domain.DTO.base;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vvvTeam.yuglightservice.domain.DTO.BaseObjDTO;
import com.vvvTeam.yuglightservice.domain.DTO.OrganizationInfo;
import com.vvvTeam.yuglightservice.domain.DTO.UserDTO;
import com.vvvTeam.yuglightservice.domain.DTO.WorkGroupInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrganizationInfo.class, name = "OrganizationInfo"),
        @JsonSubTypes.Type(value = WorkGroupInfo.class, name = "WorkGroupInfo"),
        @JsonSubTypes.Type(value = UserDTO.class, name = "UserInfo"),
        @JsonSubTypes.Type(value = BaseObjDTO.class, name = "BaseObjDTO")
})
public abstract class AdminDTOinfo {
}


