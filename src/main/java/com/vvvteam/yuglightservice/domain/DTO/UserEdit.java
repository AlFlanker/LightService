package com.vvvteam.yuglightservice.domain.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserEdit {
    @NotNull
    private Long id;
    @Size(min=5,max = 25,message = "имя пользователя должны быть от 5 до 25 символов!")
    private String username;
    @Size(max = 50,message = "пароль должен содержать не более 50 символов!")
    private String email;
    @NotBlank
    private String role;
    @Size(min=5,max = 20,message = "пароль должен содержать от 5 до 20 символов!")
    private String password;

    private String password_rep;
}
