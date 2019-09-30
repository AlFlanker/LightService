package com.vvvteam.yuglightservice.domain.entries;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vvvteam.yuglightservice.domain.auth.Organization;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@MappedSuperclass
@Getter @Setter
@EqualsAndHashCode(of={"id","organizationOwner","workGroup"})
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organizationOwner;

    @ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.REMOVE})
    private WorkGroup workGroup;

    @Column(name = "DateOfLastChanged")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date lastUpdate;

    @Column(name = "created", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    LocalDateTime created;

    @Column(name = "isDeleted")
    private boolean isDeleted;

    @Embedded
    private Location location;

    @PrePersist
    public void toCreate() {
        setCreated(LocalDateTime.now());
    }
}
