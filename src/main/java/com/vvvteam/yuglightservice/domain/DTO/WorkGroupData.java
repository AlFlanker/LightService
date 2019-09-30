package com.vvvteam.yuglightservice.domain.DTO;

import com.fasterxml.jackson.annotation.JsonView;
import com.vvvteam.yuglightservice.domain.viewSetting.UsersView;
import com.vvvteam.yuglightservice.domain.DTO.base.AdminDTOinfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@JsonView(UsersView.UsersForView.class)
public class  WorkGroupData<T> extends AdminDTOinfo {
    private String status;
    private Set<T> data;
}
