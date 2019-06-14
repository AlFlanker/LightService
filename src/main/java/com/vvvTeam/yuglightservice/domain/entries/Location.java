package com.vvvTeam.yuglightservice.domain.entries;

import com.fasterxml.jackson.annotation.JsonView;
import com.vvvTeam.yuglightservice.domain.viewSetting.BaseObjView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Location implements Serializable {
    @JsonView(BaseObjView.ObjSimpInfo.class)
    private Double latitude;
    @JsonView(BaseObjView.ObjSimpInfo.class)
    private Double longitude;
    private String address;
}
