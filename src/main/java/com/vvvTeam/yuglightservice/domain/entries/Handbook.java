package com.vvvTeam.yuglightservice.domain.entries;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;
//
@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "handbook")
public class Handbook {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String tag;
    @ElementCollection
    @CollectionTable(name = "map_of_translator")
    @MapKeyClass(value = LanguageCode.class)
    @MapKeyEnumerated(value = EnumType.STRING)
    private Map<LanguageCode,String> translator;


}
