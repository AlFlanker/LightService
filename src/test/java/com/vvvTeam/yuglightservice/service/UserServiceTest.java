package com.vvvTeam.yuglightservice.service;

import com.vvvTeam.yuglightservice.domain.auth.Role;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.repositories.UserRepo;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private PasswordEncoder passwordEncoder;


    @Test
    public void addUserFailTest(){
        User user = new User();
        user.setUsername("test");


        Mockito.doReturn(new User())
                .when(userRepo)
                .findByUsername("test");
//        boolean isUserCreated = userService.addUser(user);
//        Assert.assertFalse(isUserCreated);
//        Mockito.verify(userRepo,Mockito.times(0)).save(user);
    }

    @Test
    @Disabled
    public void activateUser() {
        User user = new User();
        user.setActivationCode("testCode");
        Mockito.doReturn(user)
                .when(userRepo)
                .findByActivationCode("activate");
        boolean isUserActivated = userService.activateUser("activate");
        Assertions.assertTrue(isUserActivated);
        Assertions.assertNull(user.getActivationCode());
        Mockito.verify(userRepo,Mockito.times(1)).save(user);
    }

    @Test
    public void activateFailTest(){
        Mockito.doReturn(null)
                .when(userRepo)
                .findByActivationCode("activate");
        boolean isUserActivated = userService.activateUser("activate");
        Assertions.assertFalse(isUserActivated);
        Mockito.verify(userRepo,Mockito
                        .times(0))
                .save(ArgumentMatchers.any());
    }
}