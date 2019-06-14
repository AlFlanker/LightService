package com.vvvTeam.yuglightservice.service.request.and.response.ForAdminTools;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class SelectItem {

    private Long id;
    @NotNull
    private TypeofItem type;
}
