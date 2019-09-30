package com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.*;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapObjectType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MapGetAll.class, name = "get_all"),
        @JsonSubTypes.Type(value = MapGetAllOrg.class, name = "get_all_org"),
        @JsonSubTypes.Type(value = MapGetAllWG.class, name = "get_all_wg"),
        @JsonSubTypes.Type(value = MapCommandChangeBrightness.class, name = "changeBrightness"),
        @JsonSubTypes.Type(value = MapCommandChangeGroupBrightness.class, name = "changeGroupBrightness"),
        @JsonSubTypes.Type(value = MapGetAllPassedObjStates.class, name = "objPassed"),
        @JsonSubTypes.Type(value = MapAddObj2Group.class, name = "add2Group"),
        @JsonSubTypes.Type(value = MapAddObj.class, name = "addObject")

})
public class MapRequest {
    @JsonIgnore @Getter @Setter
    private MapObjectType obj_type;
}


