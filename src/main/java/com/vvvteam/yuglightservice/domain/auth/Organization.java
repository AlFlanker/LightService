package com.vvvteam.yuglightservice.domain.auth;

import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.entries.ControlPoint;
import com.vvvteam.yuglightservice.domain.entries.WorkGroup;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Entity
@Data
@EqualsAndHashCode(of = {"id","name"})
@Table(name = "Organization")
public class Organization implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter @NonNull
    @Length(max = 40)
    @Column(unique = true)
    private String name;


    @Setter
    @OneToMany(mappedBy = "owner")
    private List<NetData> netData = new ArrayList<>();


    @Setter
    @OneToMany(
            targetEntity=WorkGroup.class,
            mappedBy = "organizationOwner",
            cascade={CascadeType.ALL},fetch = FetchType.EAGER)
    private Set<WorkGroup> workGroups = new HashSet<>();
    @Setter
    @OneToMany(mappedBy = "organizationOwner")
    private Set<User> users = new HashSet<>();

    @Setter
    @OneToOne
    @JoinColumn(name = "user_id")
    private User groupFounder;


    @Setter
    @OneToMany(mappedBy="organizationOwner",
            targetEntity=Lamp.class,
            cascade={CascadeType.ALL},
            fetch = FetchType.LAZY)
    private Set<Lamp> objects = new HashSet<>();

    @Setter
    @OneToMany(mappedBy="organizationOwner",
            targetEntity=ControlPoint.class,
            cascade={CascadeType.ALL},
            fetch = FetchType.LAZY)
    private Set<ControlPoint> controlPoints = new HashSet<>();

    private LocalDateTime created;

    private LocalDateTime updated;

    @Setter
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
