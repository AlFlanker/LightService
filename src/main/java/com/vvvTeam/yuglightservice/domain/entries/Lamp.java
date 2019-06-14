package com.vvvTeam.yuglightservice.domain.entries;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.vvvTeam.yuglightservice.domain.viewSetting.BaseObjView;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//import org.hibernate.validator.constraints.Length;


@Entity
@Table(name = "Lamp")
@Getter @Setter
@EqualsAndHashCode(callSuper = true,of = {"name","alias"})
@AllArgsConstructor
@NoArgsConstructor
public class Lamp extends BaseObject {

    @Length(max = 23)
    @JsonView(BaseObjView.ObjSimpInfo.class)
    @Column(name = "ObjectName")
    private String name;

    @JsonView(BaseObjView.ObjSimpInfo.class)
    @Length(min = 5,max = 60) // See in LConfig
    private String alias;
    @JsonIgnore
    @OneToMany(mappedBy = "lamp", fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.REMOVE},orphanRemoval = true)
    private List<StateObj> objStates = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cp_id")
    private ControlPoint cp_owner;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Group group;
}




