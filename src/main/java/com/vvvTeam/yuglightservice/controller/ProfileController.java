package com.vvvTeam.yuglightservice.controller;

import com.vvvTeam.yuglightservice.controller.Util.ControllerUtil;
import com.vvvTeam.yuglightservice.domain.DTO.UserProfileForm;
import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.auth.Vega.UserSessionData;
import com.vvvTeam.yuglightservice.repositories.OrganizationRepo;
import com.vvvTeam.yuglightservice.service.UserService;
import com.vvvTeam.yuglightservice.service.request.and.response.Request.ProfileObj;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.ResponceNetData;
import com.vvvTeam.yuglightservice.domain.auth.NetData;
import com.vvvTeam.yuglightservice.domain.auth.TypeOfService;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.repositories.NetDataRepo;
import com.vvvTeam.yuglightservice.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final NetDataRepo netDataRepo;
    private final UserRepo userRepo;
    private final UserSessionData userSessionData;
    private final OrganizationRepo organizationRepo;
    private final UserService userService;


    //страничка смены имени пользователя / пароля
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("username",user.getUsername());
        model.addAttribute("email",user.getEmail());
        model.addAttribute("roles",user.getRoles());
        return "profile";
    }


    //обновление данных
    @RequestMapping(value = "/profile/user/save", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> saveUserProfileForm(@AuthenticationPrincipal User user,
                                    @Valid @RequestBody UserProfileForm userProfileForm,
                                    BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtil.getErrorsMap(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMap);
        }

        if(Objects.nonNull(userService.saveUserProp(userProfileForm,user))){
            return ResponseEntity.ok(userProfileForm);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    public ResponseEntity<?> getNetData(@AuthenticationPrincipal User user){
        List<NetData> netDataList = netDataRepo.findByOwner(user.getOrganizationOwner());
        List<ResponceNetData> response = new ArrayList<>();
        if(netDataList!=null) {
            for(NetData data: netDataList){
                response.add(new ResponceNetData(data.getToken(),data.getLogin(),data.getPassword(),data.getTypeOfService(),data.isActive(),data.getAddress(),data.getAddress2(),data.getWsAddress()));
            }
        }

        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    @GetMapping("/profile/getNetData")
    public ResponseEntity<?> getNetDataByOrg(@AuthenticationPrincipal User user,@RequestParam(value = "id",required = false) Organization organization ){

        List<NetData> netDataList = netDataRepo.findByOwner(Objects.isNull(organization)?user.getOrganizationOwner():organization);
        List<ResponceNetData> response = new ArrayList<>();
        if(Objects.nonNull(netDataList)) {
            for(NetData data: netDataList){
                response.add(new ResponceNetData(data.getToken(),data.getLogin(),data.getPassword(),data.getTypeOfService(),data.isActive(),data.getAddress(),data.getAddress2(),data.getWsAddress()));
            }
        }

        return ResponseEntity.ok(response);
    }
    //сохранение параметров источников данных
    @Transactional
    @RequestMapping(value = "/profile/save_profile", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> saveProfile(@AuthenticationPrincipal User user,
                                         @Valid @RequestBody ProfileObj message,
                                         BindingResult bindingResult,
                                         Model model) throws ParseException {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtil.getErrorsMap(bindingResult);
            model.mergeAttributes(errorsMap);
        }
        Organization organization = user.getOrganizationOwner();
        if(!Objects.isNull(message.getId())){
            organization = organizationRepo.findById(message.getId()).orElse(user.getOrganizationOwner());
        }
        NetData data = netDataRepo.findByOwnerAndTypeOfService(organization, TypeOfService.valueOf(message.getService()));
        if(Objects.nonNull(data)) {
            data.setToken(message.getNet868_token());
            data.setLogin(message.getVega_username());
            data.setPassword(message.getVega_password());
            data.setAddress(message.getAddress());
            data.setAddress2(message.getAddress2());
            data.setWsAddress(message.getWss());
            data.setOwner(organization);
            // incorrect parse boolean ???
            data.setActive(message.getIsActive().equals("true"));
            try {
                if (!data.isActive()) {
                    if (userSessionData.getSessions().containsKey(organization.getId())) {
                        WebSocketSession webSocketSession = userSessionData.getSessions().get(organization.getId());
                        if(webSocketSession.isOpen()) {
                            webSocketSession.close(CloseStatus.GOING_AWAY);
                            log.info("webSocketSession id = "+webSocketSession.getId()+" is closed");
                        }
                    }
                }
            }
            catch (IOException e){
                log.debug(e.getMessage());
            }
            netDataRepo.save(data);
        }
        else {
            NetData netData = new NetData();
            netData.setTypeOfService(TypeOfService.valueOf(message.getService()));
            netData.setLogin(message.getVega_username());
            netData.setPassword(message.getVega_password());
            netData.setToken(message.getNet868_token());
            netData.setAddress(message.getAddress());
            netData.setAddress2(message.getAddress2());
            netData.setWsAddress(message.getWss());
            netData.setOwner(organization);
            netData.setActive(message.getIsActive().equals("true"));
            netDataRepo.save(netData);
        }
        return ResponseEntity.ok("{\"status\":\"ok\"}");
    }


}
