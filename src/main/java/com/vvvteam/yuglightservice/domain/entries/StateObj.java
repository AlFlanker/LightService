package com.vvvteam.yuglightservice.domain.entries;



import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode
@Getter @Setter
@Entity
@Table(name = "States")
public class StateObj implements Serializable {
    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String type;

    private byte flags;

    @Embedded
    private RawData data;

    @Column(name = "DateOfChanged",updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date currentDate;
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private Lamp lamp;
    @EqualsAndHashCode.Exclude
    @Column(name = "IsDeleted")
    private boolean isDeleted;

    @EqualsAndHashCode.Exclude
    @Column(name = "acknowledged")
    private boolean isAck;

    @EqualsAndHashCode.Exclude
    @Column(name = "isPassed")
    private boolean passed;



}
