package com.vvvteam.yuglightservice.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UserGM {
    private String username;
    private String role;
    private String organization;
    private String workGroup;
}
