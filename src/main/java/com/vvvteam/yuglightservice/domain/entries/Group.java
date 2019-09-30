package com.vvvteam.yuglightservice.domain.entries;

import com.fasterxml.jackson.annotation.JsonView;
import com.vvvteam.yuglightservice.domain.viewSetting.GroupView;
import com.vvvteam.yuglightservice.domain.auth.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "Groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obj_id")
    private Lamp mainNode;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Lamp> nodes = new ArrayList<>();
    @JsonView(GroupView.visible.class)

    @ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.REMOVE})
    private WorkGroup workGroup;

    private String nameOfGroup;

    @JsonView(GroupView.visible.class)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userOwner;

    private boolean isDeleted;


}
