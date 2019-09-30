package com.vvvteam.yuglightservice.domain.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAdd {
    private long wg;
    @Size(min=5,max = 25,message = "имя пользователя должны быть от 5 до 25 символов!")
    private String username;
    @Size(min=5,max = 20,message = "пароль должен содержать от 5 до 20 символов!")
    private String password;
    @Size(max = 50,message = "пароль должен содержать не более 50 символов!")
    @Email(message = "некореектный email")
    private String email;
}
