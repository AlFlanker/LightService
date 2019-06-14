package com.vvvTeam.yuglightservice.controller;

import com.vvvTeam.yuglightservice.domain.DTO.AllTags;
import com.vvvTeam.yuglightservice.domain.DTO.report.AllObj;
import com.vvvTeam.yuglightservice.domain.auth.Role;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.domain.entries.ControlPoint;
import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import com.vvvTeam.yuglightservice.domain.entries.dto.RawDataDto;
import com.vvvTeam.yuglightservice.service.interfaces.LampService;
import com.vvvTeam.yuglightservice.service.interfaces.StateService;
import com.vvvTeam.yuglightservice.service.request.and.response.report.ReportBody;
import com.vvvTeam.yuglightservice.service.request.and.response.report.ReportResponce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/report")
public class ReportController {
    private final LampService objectsService;
    private final StateService stateService;

    /**
     * Вернуть все поля Состояния ламп
     *
     * @return типы
     */

    @GetMapping("/type")
    public ResponseEntity<?> getType() {
        List<AllTags> list = new ArrayList<>();
        List<String> fieldsname = Arrays.stream(RawDataDto.class.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
        fieldsname.add("id");
        fieldsname.add("object_name");
        fieldsname.add("cp_id");
        AllTags tags = new AllTags(Lamp.class.getSimpleName().toLowerCase(), fieldsname);
        list.add(tags);
        fieldsname = new ArrayList<>();
        fieldsname.add("id");
        fieldsname.add("objectName");
        tags = new AllTags(ControlPoint.class.getSimpleName().toLowerCase(), fieldsname);
        list.add(tags);
        return ResponseEntity.ok(list);
    }

    /**
     * @return ReportConstructor
     */
    @GetMapping()
    public String getReportConstructor() {
        return "states";
    }

    /**
     * @param user
     * @param type тип возвращаемого объекта
     * @return филды объекта
     */
    @GetMapping("/objects")
    public ResponseEntity getObjects(@AuthenticationPrincipal User user, @RequestParam(value = "type", defaultValue = "lamp") String type) {
        if(user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.SuperUserOwner)){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Для Админа и владельцев пока не сделано!");
        }
        if (type.equals("lamp")) {
            List<Object[]> lamp = Optional.of(objectsService.findAllLamp(user.getWorkGroup())).orElse(Collections.emptyList());
            List<AllObj> arr = new ArrayList<>();
            AllObj groupData = new AllObj();
            groupData.setType("lamp");
            groupData.setFields(lamp);
            arr.add(groupData);
            return ResponseEntity.ok(arr);
        } else if (type.equals("cp")) {
            List<Object[]> lamp = Optional.of(objectsService.findAllCP(user.getWorkGroup())).orElse(Collections.emptyList());
            List<AllObj> arr = new ArrayList<>();
            AllObj groupData = new AllObj();
            groupData.setType("cp");
            groupData.setFields(lamp);
            arr.add(groupData);
            return ResponseEntity.ok(arr);
        } else return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();


    }


    /**
     * @param user      текущая сессия
     * @param type      тип КП,лампа и.т.д
     * @param datefrom  дата начала выборки
     * @param dateto    дата конца выборки
     * @param objectsid id объектов
     * @param tags      поля объектов
     * @param limit     ограничение количества
     * @param sort      пока не использую
     * @return состояния выбранного(-ых) объектов
     */
    @GetMapping("/allstates")
    public ResponseEntity getstates(@AuthenticationPrincipal User user,
                                    @RequestParam(value = "type", required = false, defaultValue = "lamp") String type,
                                    @RequestParam("datefrom") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date datefrom,
                                    @RequestParam(value = "dateto") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") Date dateto,
                                    @RequestParam("objectsid") List<Long> objectsid,
                                    @RequestParam("tags") Set<String> tags,
                                    @RequestParam(value = "limit", required = false, defaultValue = "2000000") long limit,
                                    @RequestParam(value = "sort", required = false, defaultValue = "ASC") String sort
    ) {

        ReportResponce reportResponce = new ReportResponce();
        if (datefrom.after(dateto)) {
            return ResponseEntity.badRequest().body("date_of_changed wrong order");
        }
        if (objectsid.size() == 0 || tags.size() == 0) {
            return ResponseEntity.badRequest().body("the field(tags and id) cannot be empty");
        }
        try {
            for (String tag : tags) {
                RawDataDto.Fields.valueOf(tag);
            }
        } catch (IllegalArgumentException e) {
            log.debug(e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        if ("lamp".equals(type)) {
            List<Lamp> lamps = Optional.of(objectsService.findLampByInId(objectsid))
                    .orElse(Collections.emptyList());
            if (lamps.size() == 0) {
                return ResponseEntity.noContent().build();
            }
            List<ReportBody> reportBodies = new ArrayList<>();
            ReportBody body;
            for (Lamp lamp : lamps) {
                body = new ReportBody(lamp.getId(), stateService.getStatesBetween(lamp, datefrom, dateto, limit, tags));
                reportBodies.add(body);
            }
            reportResponce.setBody(reportBodies);
            return ResponseEntity.ok(reportResponce);
        } else
            return ResponseEntity.noContent().build();
    }


}
