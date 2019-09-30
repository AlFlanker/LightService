package com.vvvteam.yuglightservice.domain.DTO;

import com.vvvteam.yuglightservice.domain.DTO.annotation.FieldMatch;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@FieldMatch(first = "password",second = "repPassword",message = "Пароль должны совпадать!")
public class UserProfileForm {
    @NotBlank(message = "не может быть пустым")
    private String name;
    private String email;
    private String password;
    private String repPassword;
}
