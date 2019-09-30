package com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.MapExecutable;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.Support.AddObj2Group;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapRequest;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.base.MapType;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.MapResponseAddObj2Group;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapObjectType;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapResponse;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Response.base.MapStatus;
import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.entries.Group;
import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.repositories.GroupRepository;
import com.vvvteam.yuglightservice.repositories.LampRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Init bean через сетеры из-за необходимости конструктора без параметров для Jackson!
 */
@Scope("prototype")
@Component
@NoArgsConstructor
@Slf4j
public class MapAddObj2Group extends MapRequest implements MapExecutable {
    @JsonIgnore
    private LampRepository lampRepository;
    @JsonIgnore
    private GroupRepository groupRepository;

    @Autowired
    public void setLampRepository(LampRepository lampRepository) {
        this.lampRepository = lampRepository;
    }

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Getter
    @Setter
    private AddObj2Group payload;

    @Override
    public MapResponse execute(User user, MapType objectType) {
        switch (objectType) {
            case CP:
                return null;
            case Lamp:
                return addLampToGroup();
            case Group:
                break;
        }
        return null;

    }

    @Transactional(rollbackFor = NoSuchElementException.class)
    public MapResponseAddObj2Group addLampToGroup() {
        Group group = null;
        List<Lamp> lamps = lampRepository.findByNameIn(payload.getEui()).orElse(Collections.emptyList());
        if(!StringUtils.isEmpty(payload.getGroupsName())){
        group = groupRepository.findById(Long.valueOf(payload.getGroupsName())).orElse(null);
        }
        MapResponseAddObj2Group response = new MapResponseAddObj2Group();
        response.setType(MapObjectType.add_lamp_to_group);
        response.setStatus(MapStatus.success);
        if (lamps.size() > 0 ) {
            for (Lamp lamp : lamps) {
                lamp.setGroup(group);
            }
            lampRepository.saveAll(lamps);
            response.setStatus(MapStatus.success);

            response.setPayload(payload);
            return response;
        }
        else {
            response.setStatus(MapStatus.bad_data);
            response.setPayload(payload);
            return response;
        }
    }
}
