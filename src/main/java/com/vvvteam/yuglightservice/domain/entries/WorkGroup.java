package com.vvvteam.yuglightservice.domain.entries;

import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.auth.User;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Setter @Getter
@EqualsAndHashCode(of={"id","name"})
@NoArgsConstructor
public class WorkGroup implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String name;

    @ManyToOne @JoinColumn(name = "organization_id")
    private Organization organizationOwner;

    /*основатель группы*/
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User wgFounder;


    @OneToMany(mappedBy = "workGroup",
            cascade={CascadeType.PERSIST},
            fetch = FetchType.LAZY,
            targetEntity=Lamp.class)
    private List<Lamp> wgLamps = new ArrayList<>();

    @OneToMany(mappedBy = "workGroup",
            cascade={CascadeType.REMOVE},
            fetch = FetchType.LAZY,
            targetEntity=Group.class)
    private List<Group> wgGroups = new ArrayList<>();


    @OneToMany( mappedBy = "workGroup",
            cascade={CascadeType.PERSIST},
            fetch = FetchType.LAZY,
            targetEntity=ControlPoint.class)
    private List<ControlPoint> wgCps = new ArrayList<>();

    @OneToMany(mappedBy="workGroup",
    targetEntity=User.class,
    cascade={CascadeType.PERSIST},
    fetch = FetchType.EAGER)
    private Set<User> usersFromWG = new HashSet<>();

    private LocalDateTime created;

    private LocalDateTime updated;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
    @Column(name = "created", updatable = false)
    public LocalDateTime getCreated() {
        return created;
    }

    @Column(name = "updated", insertable = false)
    public LocalDateTime getUpdated() {
        return updated;
    }

    @PrePersist
    public void toCreate() {
        setCreated(LocalDateTime.now());
    }

    @PreUpdate
    public void toUpdate() {
        setUpdated(LocalDateTime.now());
    }




}
