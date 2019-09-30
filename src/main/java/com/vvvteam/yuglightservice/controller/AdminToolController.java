package com.vvvteam.yuglightservice.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.vvvteam.yuglightservice.domain.DTO.*;
import com.vvvteam.yuglightservice.domain.DTO.registrateDTO.OrganizationRegistrate;
import com.vvvteam.yuglightservice.domain.DTO.registrateDTO.WorkGroupDTO;
import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.auth.Role;
import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.domain.entries.ControlPoint;
import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.entries.WorkGroup;
import com.vvvteam.yuglightservice.domain.viewSetting.UsersView;
import com.vvvteam.yuglightservice.exceptions.organization.OrganizationAlreadyExist;
import com.vvvteam.yuglightservice.exceptions.user.UserAlreadyExistException;
import com.vvvteam.yuglightservice.exceptions.workGroup.WorkGroupAlreadyExistException;
import com.vvvteam.yuglightservice.service.UserService;
import com.vvvteam.yuglightservice.service.interfaces.AdminToolsService;
import com.vvvteam.yuglightservice.service.interfaces.OrganizationService;
import com.vvvteam.yuglightservice.service.interfaces.WorkGroupService;
import com.vvvteam.yuglightservice.service.request.and.response.ForAdminTools.SelectItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/globalManager")
public class AdminToolController {
    private final UserService userService;
    private final OrganizationService organizationService;
    private final WorkGroupService workGroupService;
    private final AdminToolsService adminToolsService;
    private final SessionRegistry sessionRegistry;


    /**
     * @return Список пользователей online
     * */
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner')")
    @GetMapping("/activeUsers")
    @Transactional
    public ResponseEntity<?> getActiveUsers(@AuthenticationPrincipal User user) {
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        List<UserGM> users;
        if (user.getRoles().contains(Role.SuperUserOwner)) {
            users = allPrincipals.stream()
                    .filter(principal -> (Objects.equals(((User) principal).getOrganizationOwner(),user.getOrganizationOwner())))
                    .map((principal) -> {
                        UserGM userGM = new UserGM();
                        userGM.setUsername(((User) principal).getUsername());
                        userGM.setRole(((User) principal).getRoles().toArray()[0].toString());
                        userGM.setOrganization(Objects.nonNull(((User) principal).getOrganizationOwner()) ? ((User) principal).getOrganizationOwner().getName() : "");
                        userGM.setWorkGroup(Objects.nonNull(((User) principal).getWorkGroup()) ? ((User) principal).getWorkGroup().getName() : "");
                        return userGM;
                    }).collect(Collectors.toList());
            return ResponseEntity.ok(users);
        } else {
            users = allPrincipals.stream().map((principal) -> {
                UserGM userGM = new UserGM();
                userGM.setUsername(((User) principal).getUsername());
                userGM.setRole(((User) principal).getRoles().toArray()[0].toString());
                userGM.setOrganization(Objects.nonNull(((User) principal).getOrganizationOwner()) ? ((User) principal).getOrganizationOwner().getName() : "");
                userGM.setWorkGroup(Objects.nonNull(((User) principal).getWorkGroup()) ? ((User) principal).getWorkGroup().getName() : "");
                return userGM;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(users);
        }

    }

    /**
     *
     * @param model
     * @param user
     * @return страницу админской панели
     */
    @GetMapping()
    public String getTools(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user_role", user.getRoles().toString());
        model.addAttribute("isDevMode", true);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("roles", user.getRoles());
        return "globalManager";
    }

    /**
     *
     * @return список организаций
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    @GetMapping("/organizations")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> getOrganizations() {
        OrganizationResponse response = new OrganizationResponse();
        response.setStatus("ok");
        List<OrganizationInfo> infoList = organizationService.getOrganizationInfo();
        response.setInfoList(infoList);
        return ResponseEntity.ok(response);
    }

    /**
     * @return Информация по выбранной организации
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @Transactional
    @GetMapping("/organizations/{id}")
    public ResponseEntity<?> getWorkGroups(@PathVariable("id") Organization organization,
                                           @RequestHeader(required = false) String referer) throws NoSuchFieldException, IllegalAccessException {
        WorkGroupResponse workGroupResponse = new WorkGroupResponse();
        return adminToolsService.getWorkGroupResponce(organization);

    }

    /**
     * @return информация организации пользотеля, который делает запрос
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @PreAuthorize("hasAuthority('SuperUserOwner')")
    @Transactional
    @GetMapping("/organization")
    public ResponseEntity<?> getOrgWorkGroup(@AuthenticationPrincipal User user) throws NoSuchFieldException, IllegalAccessException {
        WorkGroupResponse workGroupResponse = new WorkGroupResponse();
        Organization organization = user.getOrganizationOwner();
        return adminToolsService.getWorkGroupResponce(organization);

    }

    /**
     * @param user       юзер, который запросил
     * @param selectItem Что отдать:Лампы/КП/Юзеров
     * @param pageable
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @Transactional
    @GetMapping(value = "/organizations/objects")
    public ResponseEntity<?> getObjectFromWorkGroup(@AuthenticationPrincipal User user,
                                                    @Valid SelectItem selectItem,
                                                    @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return adminToolsService.getObjectFromWorkGroup(selectItem, user);

    }

    /**
     *
     * @param sessionUser
     * @param user
     * @return информация о пользователе по id
     */
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @JsonView(UsersView.UserInfo.class)
    @GetMapping(value = "/users/info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal User sessionUser, @RequestParam("id") User user) {
        return ResponseEntity.ok(user);
    }

    /**
     * @param user          админ(админ группы)
     * @param update        новые данные + id редактируемого пользователя
     * @param bindingResult
     * @return
     */
    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @RequestMapping(value = "/users/save", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> saveUserInfo(@AuthenticationPrincipal User user, @Valid @RequestBody UserEdit update, BindingResult bindingResult) {
        Map<String, String> errorsMap = new HashMap<>();
        if (bindingResult.hasErrors()) {
            errorsMap = ControllerUtil.getErrorsMap(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMap);
        }
        if ((!update.getPassword().equals(update.getPassword_rep())) && (!StringUtils.isEmpty(update.getPassword()))) {
            errorsMap.put("password", "неверно подтвержили пароль!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMap);
        }
        User editableUser = userService.loadById(update.getId());
        int edit_rang = ((Role) editableUser.getRoles().toArray()[0]).getRang();
        int user_rang = ((Role) user.getRoles().toArray()[0]).getRang();
        int changeRole = Role.valueOf(update.getRole().substring(1, update.getRole().length() - 1)).getRang();
        if (edit_rang < user_rang) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("\"status\":\"пользователь имеет больше привелегий!\"");
        }
        if (user_rang > changeRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("\"status\":\"Невозможно установить больше привелегий, чем у Вас!\"");
        }

        if (Objects.nonNull(editableUser)) {
            userService.changeUserData(editableUser, update);
            errorsMap.put("status", "ok");
        }
        return ResponseEntity.ok(errorsMap);
    }


    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @RequestMapping(value = "/users/addnew", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addUser(@AuthenticationPrincipal User currentUser,
                                     @Valid @RequestBody UserAdd data,
                                     BindingResult bindingResult) {
        Map<String, String> errorsMap = new HashMap<>();
        if (bindingResult.hasErrors()) {
            errorsMap = ControllerUtil.getErrorsMap(bindingResult);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorsMap);
        }
        try {
            userService.addNewUserToGroup(data, currentUser);
        } catch (UserAlreadyExistException e) {
            errorsMap.put("user", "пользователь с таким именем уже зарегистрирован в системе");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorsMap);
        } catch (IllegalArgumentException e) {
            errorsMap.put("workroup", "группа была удалена!");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorsMap);
        }
        errorsMap.put("status", "ok");
        return ResponseEntity.status(HttpStatus.CREATED).body(errorsMap);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/addOrganization", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addOrganization(@Valid @RequestBody OrganizationRegistrate registrate, BindingResult bindingResult) {

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

    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner')")
    @RequestMapping(value = "/addWorkGroup", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addWorkGroup(@AuthenticationPrincipal User currentUser, @Valid @RequestBody WorkGroupDTO registrate, BindingResult bindingResult) {
        Map<String, String> errorsMap = new HashMap<>();
        Organization organization;
        if (bindingResult.hasErrors()) {
            errorsMap = ControllerUtil.getErrorsMap(bindingResult);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorsMap);
        }

        try {
            workGroupService.addWorkGroup(currentUser, registrate);
        } catch (WorkGroupAlreadyExistException e) {
            errorsMap.put("nameOfWorkGroup", "группа с таким именем уже зарегистрирована в системе");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorsMap);
        } catch (UserAlreadyExistException e) {
            errorsMap.put("user", "пользователь с таким именем уже зарегистрирован в системе");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorsMap);
        }

        errorsMap.put("status", "ok");
        return ResponseEntity.status(HttpStatus.CREATED).body(errorsMap);

    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal User user, @PathVariable("id") User delUser) {
        int delRang = delUser.getRoles().iterator().next().getRang();
        int currentRang = user.getRoles().iterator().next().getRang();
        WorkGroup workGroup = workGroupService.getByUserFounder(delUser);
        try {
            if (delRang < currentRang) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else if (user.equals(delUser)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else if (delRang == currentRang) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
            }
            if (user.getRoles().contains(Role.ADMIN)) {

                if (workGroup != null) {
                    adminToolsService.deleteWorkGroup(workGroup);
                    return ResponseEntity.ok("{\"status\":\"ok\",\"wg\":\"true\"}");
                } else userService.deleteUser(delUser);
            } else if (user.getRoles().contains(Role.SuperUserOwner)) {
                if (delUser.getOrganizationOwner().equals(user.getOrganizationOwner())) {
                    if (workGroup != null) {
                        adminToolsService.deleteWorkGroup(workGroup);
                        return ResponseEntity.ok("{\"status\":\"ok\",\"wg\":\"true\"}");
                    } else userService.deleteUser(delUser);
                }
            } else if (user.getRoles().contains(Role.SuperUser)) {
                if (delUser.getWorkGroup().equals(user.getWorkGroup())) {
                    userService.deleteUser(delUser);
                }
            } else return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.warn(e.getMessage());
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("{status:\"ok\"}");
    }


    /**
     * @param user      текущий юзер
     * @param workGroup удаляемая группа
     * @return вернет 403, если не админ или удаление не своей группы
     */
    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner')")
    @DeleteMapping("/workGroup/delete/{id}")
    public ResponseEntity<?> deleteWorkGroup(@AuthenticationPrincipal User user, @PathVariable("id") WorkGroup workGroup) {
        if (user.getRoles().contains(Role.ADMIN)) {
            adminToolsService.deleteWorkGroup(workGroup);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else if (user.getRoles().contains(Role.SuperUserOwner)) {
            if (user.getOrganizationOwner().equals(workGroup.getOrganizationOwner())) {
                adminToolsService.deleteWorkGroup(workGroup);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } else return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @DeleteMapping("/lamp/delete/{id}")
    public ResponseEntity<?> deleteLamp(@AuthenticationPrincipal User user, @PathVariable("id") Lamp lamp) {
        if (Objects.nonNull(lamp)) {
            adminToolsService.deleteLamp(lamp);
        }
        return ResponseEntity.ok().body("ok");
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @DeleteMapping("/cp/delete/{id}")
    public ResponseEntity<?> deleteCP(@AuthenticationPrincipal User user, @PathVariable("id") ControlPoint cp) {
        if (Objects.nonNull(cp)) {
            adminToolsService.deleteCP(cp);
        }
        return ResponseEntity.ok().body("ok");
    }


}
