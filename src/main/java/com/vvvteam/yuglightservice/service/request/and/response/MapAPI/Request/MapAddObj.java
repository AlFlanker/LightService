package com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.MapExecutable;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapRequest;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapType;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.MapResponseAddMapGroup;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.MapResponseAddNew;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapObjectType;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapStatus;
import com.vvvteam.yuglightservice.service.request.and.response.Request.LampPropsMsg;
import com.vvvteam.yuglightservice.service.request.and.response.Responses.BaseObj4Map;
import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.domain.entries.ControlPoint;
import com.vvvteam.yuglightservice.domain.entries.Group;
import com.vvvteam.yuglightservice.repositories.GroupRepository;
import com.vvvteam.yuglightservice.service.ControlPointServiceImpl;
import com.vvvteam.yuglightservice.service.interfaces.LampService;
import com.vvvteam.yuglightservice.service.interfaces.StateService;
import com.vvvteam.yuglightservice.service.request.and.response.Responses.ResponceControlPoint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
/**
 * Init bean через сетеры из-за необходимости конструктора без параметров для Jackson!
 */
@Slf4j
@Scope("prototype")
@Component
@NoArgsConstructor
public class MapAddObj extends MapRequest implements MapExecutable {
    @JsonIgnore
    private LampService objectsService;
    @JsonIgnore
    private  StateService stateService;
    @JsonIgnore
    private  GroupRepository groupRepository;
    @JsonIgnore
    private ControlPointServiceImpl controlPointServiceImpl;
    @Autowired
    public void setObjectsService(LampService objectsService) {
        this.objectsService = objectsService;
    }
    @Autowired
    public void setStateService(StateService stateService) {
        this.stateService = stateService;
    }
    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }
    @Autowired
    public void setControlPointServiceImpl(ControlPointServiceImpl controlPointServiceImpl) {
        this.controlPointServiceImpl = controlPointServiceImpl;
    }

    @Getter
    @Setter
    private LampPropsMsg payload;

    @Transactional
    @Override
    public MapResponse execute(User user, MapType objectType) {
        switch (objectType) {
            case CP:
                try {
                    return addKP(payload, user);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case Lamp:
                try {
                    return addLamp(payload, user);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case Group:
                return addMapGroup(payload, user);

        }
        return null;
    }

    @Transactional
    public MapResponseAddMapGroup addMapGroup(LampPropsMsg message, User user) {
        MapResponseAddMapGroup response;
        response = new MapResponseAddMapGroup();
        List<Group> groups = Optional.of(groupRepository.findByWorkGroup(user.getWorkGroup()))
                .orElse(Collections.emptyList());
        if (groups.stream().anyMatch(group -> group.getNameOfGroup().equals(message.getGroupsName())) || "".equals(message.getGroupsName())) {
            response.setStatus(MapStatus.bad_data);
            response.setType(MapObjectType.Map_Group_Add);
            response.setGroupsName(payload.getGroupsName());
        } else {
            Group group = new Group();
            group.setNameOfGroup(message.getGroupsName());
            group.setDeleted(false);
            group.setUserOwner(user);
            group.setWorkGroup(user.getWorkGroup());
            Group save = groupRepository.save(group);
            response.setStatus(MapStatus.success);
            response.setType(MapObjectType.Map_Group_Add);
            response.setGroupsName(save.getNameOfGroup());
            response.setId(save.getId());
        }

        return response;
    }

    private MapResponseAddNew addLamp(LampPropsMsg message, User user) throws ParseException {

        MapResponseAddNew<BaseObj4Map> result = new MapResponseAddNew<BaseObj4Map>();
        result.setType(MapObjectType.lamp_add);
        if (!valid(message)) {
            result.setStatus(MapStatus.bad_data);
            return result;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        result.setStatus(MapStatus.success);
        result.setObject(objectsService.addNewObj(user, message));
        return result;
    }

    private boolean valid(LampPropsMsg message) {
        Pattern euiChecker = Pattern.compile("^(([0-9a-fA-F]{2})-){7}([0-9a-fA-F]{2})$");
        Pattern latLonChecker = Pattern.compile("^(\\d\\d)\\.(\\d+)$");
        if (!euiChecker.matcher(message.getEui()).matches()) {
            return false;
        }
        if (!latLonChecker.matcher(message.getLatitude()).matches()
                ||
                !latLonChecker.matcher(message.getLongitude()).matches()) {


            return false;
        }
        return !StringUtils.isEmpty(message.getAlias());
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public MapResponseAddNew addKP(LampPropsMsg message, User user) throws ParseException {
        MapResponseAddNew result = new MapResponseAddNew<ResponceControlPoint>();
        Pattern latLonChecker = Pattern.compile("^(\\d\\d)\\.(\\d+)$");

        result.setType(MapObjectType.cp_add);
        if (!latLonChecker.matcher(message.getLatitude()).matches()
                ||
                !latLonChecker.matcher(message.getLongitude()).matches()) {
            result.setStatus(MapStatus.bad_data);
            return result;
        }
        if (StringUtils.isEmpty(message.getAlias())) {
            result.setStatus(MapStatus.bad_data);
            return result;
        }
        ControlPoint controlPoint;
        if ((controlPoint = controlPointServiceImpl.addNewKP(user, message)) != null) {
            log.info("ObjectsController getNewObjectState");
            result.setStatus(MapStatus.success);
            ResponceControlPoint rcp = new ResponceControlPoint();
            rcp.setObjectName(controlPoint.getObjectName());
            rcp.setLastUpdate(controlPoint.getLastUpdate());
            rcp.setLatitude(controlPoint.getLocation().getLatitude());
            rcp.setLongitude(controlPoint.getLocation().getLongitude());
            result.setObject(rcp);
            return result;
        } else {
            result.setStatus(MapStatus.success);
            result.setObject(null);
            return result;
        }

    }
}
