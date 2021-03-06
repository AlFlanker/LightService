package com.vvvteam.yuglightservice.domain.DTO;


import com.vvvteam.yuglightservice.domain.DTO.base.AdminDTOinfo;
import com.vvvteam.yuglightservice.domain.auth.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UserDTO extends AdminDTOinfo {
    private long id;
    private String username;
    private String email;
    private List<String> groups = new ArrayList<>();
    private Role role;
}
