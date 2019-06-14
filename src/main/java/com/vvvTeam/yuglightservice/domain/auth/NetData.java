package com.vvvTeam.yuglightservice.domain.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "NetData")
public class NetData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String token;
    private String login;
    private String password;
    private String address;
    private String address2;
    private LocalDateTime created;
    private LocalDateTime updated;
    @Column(name = "ws_adr")
    private String wsAddress;
    @Enumerated(EnumType.STRING)
    @Column(name = "typeOfService")
    private TypeOfService typeOfService;
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization owner;
    private boolean isActive;
    @Column(name = "ws_active")
    private boolean wsActive;

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
