package com.vvvTeam.yuglightservice.controller;

import com.vvvTeam.yuglightservice.domain.DTO.registrateDTO.OrganizationRegistrate;
import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.exceptions.organization.OrganizationAlreadyExist;
import com.vvvTeam.yuglightservice.exceptions.user.UserAlreadyExistException;
import com.vvvTeam.yuglightservice.repositories.OrganizationRepo;
import com.vvvTeam.yuglightservice.service.UserService;
import com.vvvTeam.yuglightservice.service.interfaces.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RegistrationController {
    private final UserService userService;
    private final OrganizationService organizationService;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "registration_thy";
    }

    @ResponseBody
    @PostMapping("/registration")
    public ResponseEntity addUser(@Valid @RequestBody OrganizationRegistrate registrate,
                                  BindingResult bindingResult) {
        Map<String, String> errorsMap = new HashMap<>();

        if (bindingResult.hasErrors()) {
            errorsMap = ControllerUtil.getErrorsMap(bindingResult);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorsMap);
        }
        try {
            organizationService.registrateOrganization(registrate);
        } catch (UserAlreadyExistException e) {
            errorsMap.put("user", "пользователь с таким именем уже зарегистрирован в системе");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorsMap);
        } catch (OrganizationAlreadyExist e) {
            errorsMap.put("nameOfOrganization", "организация с таким именем уже зарегистрирована в системе");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorsMap);

        }
        errorsMap.put("status", "ok");
        return ResponseEntity.status(HttpStatus.CREATED).body(errorsMap);
    }


    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);
        if (isActivated) {
            model.addAttribute("message", "user successfulle activated");
        } else {
            model.addAttribute("message", "activation code is not found!");
        }
        return "login";
    }


}
