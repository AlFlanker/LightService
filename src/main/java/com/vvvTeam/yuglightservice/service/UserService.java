package com.vvvTeam.yuglightservice.service;


import com.vvvTeam.yuglightservice.domain.DTO.UserAdd;
import com.vvvTeam.yuglightservice.domain.DTO.UserEdit;
import com.vvvTeam.yuglightservice.domain.DTO.UserProfileForm;
import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.auth.Role;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.domain.entries.WorkGroup;
import com.vvvTeam.yuglightservice.exceptions.user.UserAlreadyExistException;
import com.vvvTeam.yuglightservice.repositories.OrganizationRepo;
import com.vvvTeam.yuglightservice.repositories.UserRepo;
import com.vvvTeam.yuglightservice.repositories.WorkGroupRepo;
import com.vvvTeam.yuglightservice.service.interfaces.UserCustomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис пользователей
 * Создание/удаление/ обновление данных о пользователях
 *  */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService, UserCustomService {
    private final UserRepo userRepo;
    private final OrganizationRepo organizationRepo;
    private final PasswordEncoder passwordEncoder;
    private final WorkGroupRepo workGroupRepo;
    private final EntityManager entityManager;




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsernameAndIsDeleted(username.trim().toLowerCase(),false);
    }


    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }


    /**
     *
     * @param newUser
     * @param organization
     * @throws UserAlreadyExistException
     * Сценарий регистрации нового админа организации.
     */
    @Transactional
    public void addUser(User newUser, Organization organization)  throws UserAlreadyExistException {
        ifExist(newUser);
        User user = this.getUserPattern(newUser,organization,Role.SuperUserOwner);
        user = userRepo.save(user);
        organization.setGroupFounder(user);
        organization.setName(organization.getName().trim().toLowerCase());
        organizationRepo.save(organization);

    }

    /**
     *
     * @param newUser
     * @param workGroup
     * @param organization
     * @throws UserAlreadyExistException
     * Сценарий регистрации нового админа рабочей группы.
     */
    @Transactional
    public void addUser(User newUser, WorkGroup workGroup, Organization organization) throws UserAlreadyExistException {
        ifExist(newUser);
        User user = this.getUserPattern(newUser,organization,Role.SuperUser);
        user = userRepo.save(user);
        workGroup.setOrganizationOwner(organization);
        workGroup.setWgFounder(user);
        workGroup.setName(workGroup.getName().trim().toLowerCase());
        workGroup = workGroupRepo.save(workGroup);
        user.setWorkGroup(workGroup);
    }

    /**
     * Добавления нового пользователя в систему
     * @param newUser
     * @throws UserAlreadyExistException
     */
    @Transactional
    public void addUser(User newUser) throws UserAlreadyExistException {
        ifExist(newUser);
        User user = this.getUserPattern(newUser,null,Role.USER);
        userRepo.save(user);
    }

    private User getUserPattern(User user, Organization organization,Role role) {
        user.setActive(true);
        user.setRoles(Collections.singleton(role));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActivationCode("");
        if(Objects.nonNull(organization)) user.setOrganizationOwner(organization);
        user.setUsername(user.getUsername().trim().toLowerCase());
        return user;
    }

    private void ifExist(User user) throws UserAlreadyExistException{
        User userFromDb = userRepo.findByUsernameAndIsDeleted(user.getUsername().trim().toLowerCase(), false);
        if (Objects.nonNull(userFromDb)) {
            throw new UserAlreadyExistException(user.getUsername() + " already exist");
        }
    }



    public Set<String> getUsersNameByWorkGroups(Set<WorkGroup> groups) {
        Set<User> names = new HashSet<>();
        for (WorkGroup group : groups) {
            if (Objects.nonNull(group.getUsersFromWG())) {
                names.addAll(group.getUsersFromWG());
            }
        }
        return names.stream().map(User::getUsername).collect(Collectors.toSet());
    }


    /**
     *
     * @param code
     * @return
     * Сценарий активации пользователя. Сейчас не используется.
     */
    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);

        if (user == null) {
            return false;
        }

        user.setActivationCode(null);

        userRepo.save(user);

        return true;
    }

    public User getUserByUserDetails(UserDetails user) {
        return userRepo.findByUsernameAndPassword(user.getUsername(), user.getPassword());
    }

    public long countByWorkGroup(WorkGroup group) {
        return userRepo.countByWorkGroup(group);
    }

    public long countByWorkGroup(WorkGroup group,boolean deleted) {
        return userRepo.countByWorkGroupAndIsDeleted(group,deleted);
    }

    public List<User> loadUsersByWG(WorkGroup group) {
        return userRepo.findByWorkGroupAndIsDeleted(group,false);
    }


    public User saveUser(User user) {
        return userRepo.save(user);
    }

    /**
     *
     * @param form
     * @param user
     * @return обновленного юзера
     * Обновление данных пользователя
     */
    public User saveUserProp(UserProfileForm form,User user){
        user.setUsername(form.getName());
        if(!StringUtils.isEmpty(form.getPassword()) && form.getPassword().equals(form.getRepPassword())){
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        user.setEmail(form.getEmail());
        user.setUsername(user.getUsername().trim().toLowerCase());
        return userRepo.save(user);
    }



    public User loadById(long id) {
        return userRepo.findById(id);
    }


    /**
     * @param data        - данные от фронта (о новом ползователе)
     * @param currentUser - текущий пользователь, необходим для случае когда не передан id рабочей группы - берем из него
     * @throws UserAlreadyExistException - если есть такой пользователь
     * Сценирий добавления пользователя в Рабочую группу
     */
    @Transactional
    public void addNewUserToGroup(UserAdd data, User currentUser) throws UserAlreadyExistException,IllegalArgumentException {
        User user = new User();
        user.setUsername(data.getUsername().trim().toLowerCase());
        user.setPassword(data.getPassword());
        user.setEmail(data.getEmail());
        if (data.getWg() < 1) {
            user.setWorkGroup(currentUser.getWorkGroup());
        } else {
            WorkGroup workGroup = workGroupRepo.findById(data.getWg()).orElseThrow(IllegalArgumentException::new);
            if(workGroup.isDeleted()) throw new IllegalArgumentException("группа удалена!");
            user.setWorkGroup(workGroup);
            user.setOrganizationOwner(workGroup.getOrganizationOwner());
        }
        this.addUser(user);
    }


    @Transactional
    public void deleteUser(User user) {
        user.setDeleted(true);
        user.setActive(false); // деактивирую юзера, что б не было возможности залогиниться удаленным
        user.getGroups()
                .forEach(group -> group.setDeleted(true));
        userRepo.save(user);
    }

    public User getUserById(long id) {
        return userRepo.findById(id);
    }


    /**
     *
     * @param user текущий юзер, параметры которого редактируем
     * @param userData данные(почта, имя, роль, пароль)
     * @return обновленные данные о пользователе
     * ToDo: вынести работу с БД в Кастомный репозиторий
     */
    @Transactional
    @Override
    public User changeUserData(User user, UserEdit userData) {
        if(!StringUtils.isEmpty(userData.getUsername()))user.setUsername(userData.getUsername().trim().toLowerCase());
        if(!StringUtils.isEmpty(userData.getEmail())) user.setEmail(userData.getEmail());
        if((!StringUtils.isEmpty(userData.getPassword())) && userData.getPassword().equals(userData.getPassword_rep())){
            user.setPassword(passwordEncoder.encode(userData.getPassword()));
        }
        final String s = Role.valueOf(userData.getRole().substring(1,userData.getRole().length()-1)).toString();
        try {
            Query nativeQuery = entityManager.createNativeQuery("DELETE FROM user_role as ur WHERE ur.user_id = " + user.getId());
            nativeQuery.executeUpdate();
            nativeQuery = entityManager.createNativeQuery("INSERT INTO user_role (user_id, roles) VALUES ( " + user.getId() + ", \'" + s + "\' )");
            nativeQuery.executeUpdate();
        }
        catch (Exception e){
            log.error(e.toString());
        }
        return saveUser(user); // сохрнаить имя/почту
    }

}
