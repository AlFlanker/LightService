package com.vvvteam.yuglightservice.domain.auth;

import com.fasterxml.jackson.annotation.JsonView;
import com.vvvteam.yuglightservice.domain.entries.Group;
import com.vvvteam.yuglightservice.domain.entries.WorkGroup;
import com.vvvteam.yuglightservice.domain.viewSetting.UsersView;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "usr")
@Getter
@EqualsAndHashCode(of = {"username", "password", "roles"})
public class User implements UserDetails {
    @JsonView(UsersView.UserInfo.class)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    @JsonView(UsersView.UserInfo.class)
    @Setter
    @Length(min= 5,max = 25)
    @NotBlank(message = "Username can't be empty!")
    private String username;

    @NonNull
    @Setter
    @NotBlank(message = "password can't be empty!")
    private String password;

    @Setter
    private boolean active;

    @Setter
    @JsonView(UsersView.UserInfo.class)
    private String email;

    @NonNull
    @Setter
    private String activationCode;
    @Setter
    @JsonView(UsersView.UserInfo.class)
    @OneToMany(mappedBy = "userOwner",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Group> groups = new ArrayList<>();

    @Setter
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organizationOwner;

    @Setter
    @ManyToOne
    @JoinColumn(name = "work_group_id")
    private WorkGroup workGroup;

    @Setter
    @JsonView(UsersView.UserInfo.class)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<Role>();

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


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }


}
