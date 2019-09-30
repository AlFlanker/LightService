package com.vvvteam.yuglightservice.domain.DTO.registrateDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
public class OrganizationRegistrate {
    @Size(min=5,max = 25,message = "имя пользователя должны быть от 5 до 25 символов!")
    private String username;
    @Size(min=5,max = 20,message = "пароль должен содержать от 5 до 20 символов!")
    private String password;
    @Size(max = 50,message = "email должен содержать не более 50 символов!")
    @Email(message = "некореектный email")
    private String email;
    @Size(min=5,max = 40,message = "название организации должны быть от 5 до 40 символов!")
    private String organization;
}
