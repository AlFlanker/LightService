package com.vvvteam.yuglightservice.controller;

import com.vvvteam.yuglightservice.domain.auth.Role;
import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.domain.entries.ControlPoint;
import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.exceptions.LampAlreadyExistException;
import com.vvvteam.yuglightservice.service.interfaces.ControlPointService;
import com.vvvteam.yuglightservice.service.interfaces.LampService;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.rest_api.*;
import com.vvvteam.yuglightservice.service.request.and.response.Responses.BaseObj4Map;
import com.vvvteam.yuglightservice.service.request.and.response.Responses.ResponceControlPoint;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
@Slf4j
@Controller
@AllArgsConstructor
public class MapController {
    private final LampService lampService;
    private final ControlPointService controlPointService;

    /**
     *
     * @return страничку карты с указанием режима работы
     */
    @GetMapping("/map")
    public String showMap(@AuthenticationPrincipal User user, Model model) {
        if (user.getRoles().contains(Role.USER)) {
            model.addAttribute("mode", "view");
            return "map";
        } else {
            model.addAttribute("role", user.getRoles().toArray()[0].toString());
            model.addAttribute("mode", "edit");
            return "map";
        }
    }

    /**
     * Сменить КП для ламп
     * @return изменил КП или нет
     */
    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @RequestMapping(value = "/map/change_cp", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> saveUserInfo(@AuthenticationPrincipal User user, @RequestBody Map<String, String> params) {
        if (StringUtils.isEmpty(params.get("eui"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ControlPoint controlPoint = null;
        if (!StringUtils.isEmpty(params.get("cp"))) {
            controlPoint = controlPointService.getById(Long.valueOf(params.get("cp")));
        }
        Lamp lamp = lampService.getByEUI(params.get("eui"));
        if (Objects.nonNull(controlPoint))
            if (!(controlPoint.getWorkGroup().equals(lamp.getWorkGroup())))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        BaseObj4Map obj4Map = lampService.changeLampCP(lamp, controlPoint);
        return ResponseEntity.ok(obj4Map);
    }

    /**
     * Изменить свойства лампы
     * @return измененые данные или ошибку
     */
    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @RequestMapping(value = "/map/update_lamp", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateLampProperties(@AuthenticationPrincipal User user, @Valid @RequestBody UpdateLampProp message, BindingResult bindingResult) {
        Map<String, String> errorsMap;
        if (bindingResult.hasErrors()) {
            errorsMap = ControllerUtil.getErrorsMap(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMap);
        } else {
            try {
                Lamp lamp = lampService.updateLamp(message);
                if (lamp != null) {
                    List<BaseObj4Map> lamps = lampService.getObjWithoutLastStates(Collections.singletonList(lamp));
                    UpdateLampResponce responce = new UpdateLampResponce();
                    responce.setOld_eui(message.getEui());
                    responce.setNew_eui(message.getNew_eui());
                    responce.setData(lamps.get(0));
                    return ResponseEntity.ok(responce);
                } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } catch (LampAlreadyExistException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

        }
    }

    /**
     * Добавить новую лампу
     * @return добавленную лампу или ошибку 400
     */
    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @RequestMapping(value = "/map/lamp", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addLampFromMap(@AuthenticationPrincipal User user, @Valid @RequestBody AddLampFromMap lamp, BindingResult bindingResult) {
        Map<String, String> errorsMap = new HashMap<>();
        if (bindingResult.hasErrors()) {
            errorsMap = ControllerUtil.getErrorsMap(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMap);
        }

        BaseObj4Map save_lamp = lampService.addLamp(lamp, user);
        if (Objects.nonNull(save_lamp))
            return ResponseEntity.ok().body(save_lamp);
        else {
            errorsMap.put("eui", "already exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMap);
        }


    }


    /**
     * Обновление параметров лампы в режиме "пересечения"
     * @return измененные лампы или ошибку 400
     */
    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @RequestMapping(value = "/map/update/lamp", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateLampFromMap(@AuthenticationPrincipal User user, @Valid @RequestBody List<UpdateLampFromMap> lamps, BindingResult bindingResult) {
        Map<String, String> errorsMap = new HashMap<>();
        BaseObj4Map baseObj4Map;
        List<BaseObj4Map> all = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            errorsMap = ControllerUtil.getErrorsMap(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMap);
        }
        for (UpdateLampFromMap lamp : lamps) {
            baseObj4Map = lampService.updateLamp(lamp, user);
            if (Objects.isNull(baseObj4Map)) {
                errorsMap.put("eui", "already exist");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMap);
            }
            all.add(baseObj4Map);
        }
        return ResponseEntity.ok().body(all);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @RequestMapping(value = "/map/delete/lamp/{eui}", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> deleteLamp(@AuthenticationPrincipal User user, @PathVariable("eui") String eui) {
        if (Objects.nonNull(eui)) {
            lampService.deleteLamp(eui);
            Map param = new HashMap<String, String>();
            param.put("eui", eui);
            return ResponseEntity.ok().body(param);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

    }

    /**
     * Добавить КП
     * @param user
     * @param cp
     * @param bindingResult
     * @return КП или ошибка 400
     */
    @Transactional
    @PreAuthorize("hasAnyAuthority('ADMIN','SuperUserOwner','SuperUser')")
    @RequestMapping(value = "/map/controlpoint", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addControlPointFromMap(@AuthenticationPrincipal User user, @Valid @RequestBody AddControlPointFromMap cp, BindingResult bindingResult) {
        Map<String, String> errorsMap = new HashMap<>();
        if (bindingResult.hasErrors()) {
            errorsMap = ControllerUtil.getErrorsMap(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMap);
        }

        ResponceControlPoint save_cp = controlPointService.addControlPointFromMap(user, cp);
        if (Objects.nonNull(save_cp)) {
            return ResponseEntity.ok().body(save_cp);
        } else {
            errorsMap.put("cp", "already exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsMap);
        }

    }


}
