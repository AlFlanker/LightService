package com.vvvteam.yuglightservice.domain.entries;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ControlPoints")
public class ControlPoint extends BaseObject {

    @OneToMany(mappedBy = "cp_owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Lamp> lamps;
    @Length(max = 30)
    @Column(name = "object_name")
    private String objectName;


}
