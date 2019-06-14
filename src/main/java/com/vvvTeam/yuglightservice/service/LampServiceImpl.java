package com.vvvTeam.yuglightservice.service;

import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.auth.Role;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.domain.entries.*;
import com.vvvTeam.yuglightservice.domain.support.LampByEui;
import com.vvvTeam.yuglightservice.domain.support.LampsLastStates;
import com.vvvTeam.yuglightservice.exceptions.ControlPointNotFoundException;
import com.vvvTeam.yuglightservice.exceptions.LampAlreadyExistException;
import com.vvvTeam.yuglightservice.exceptions.organization.OrganizationNotFound;
import com.vvvTeam.yuglightservice.exceptions.workGroup.WorkGroupNotFoundException;
import com.vvvTeam.yuglightservice.repositories.*;
import com.vvvTeam.yuglightservice.repositories.support.LampEuiRepo;
import com.vvvTeam.yuglightservice.repositories.support.LampsStatesRepo;
import com.vvvTeam.yuglightservice.service.interfaces.LampService;
import com.vvvTeam.yuglightservice.service.interfaces.StateService;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.rest_api.AddLampFromMap;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.rest_api.UpdateLampFromMap;
import com.vvvTeam.yuglightservice.service.request.and.response.MapAPI.rest_api.UpdateLampProp;
import com.vvvTeam.yuglightservice.service.request.and.response.Request.LampPropsMsg;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.BaseObj4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.Groups4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.Organization4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.WorkGroup4Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class LampServiceImpl implements LampService {
    private final ControlPointsRepo controlPointsRepo;
    private final GroupRepository groupRepository;
    private final OrganizationRepo organizationRepo;
    private final WorkGroupRepo workGroupRepo;
    private final StateService stateService;
    private final LampRepository lampRepository;
    private final LampsStatesRepo lampsStatesRepo;
    private final LampEuiRepo lampEuiRepo;
    private final EntityManager manager;
    private final Pattern pattern = Pattern.compile("^(([0-9a-fA-F]{2})-){7}([0-9a-fA-F]{2})$");

    @Override
    public List<Lamp> getAllLampByWorkGroup(WorkGroup group) {
        return Optional.of(lampRepository.findByWorkGroupAndIsDeleted(group, false)).orElse(Collections.emptyList());
    }


    @Override
    public List<BaseObj4Map> getObjWithoutLastStates(List<Lamp> lamps) {
        return getObj4Map(lamps);
    }

    /**
     * Запрос последних неотправленных(!!!) клиенту данных по лампам
     *
     * @param lamps
     * @return List<DTO>
     */
    @Override
    public List<BaseObj4Map> getObj4MapWithLastStates(List<Lamp> lamps) {
        List<BaseObj4Map> result = new ArrayList<>();
        List<StateObj> states;
        BaseObj4Map tmp;
        for (Lamp b : lamps) {
            tmp = convert2Obj4Map(b);
            states = stateService.getNotPassedStates(b);
            tmp.setObjStates(states);
            result.add(tmp);
        }
        return result;
    }


    /**
     * Конвертер List<Lamp> -> List<DTO>
     *
     * @param lamps
     * @return
     */
    @Override
    public List<BaseObj4Map> getObj4Map(List<Lamp> lamps) {
        List<BaseObj4Map> list2send = new ArrayList<>(lamps.size());
        for (Lamp lamp : lamps) {
            list2send.add(convert2Obj4Map(lamp));
        }
        return list2send;
    }

    /**
     * Конвертер Lamp в DTO
     *
     * @param lamp
     * @return
     */
    private BaseObj4Map convert2Obj4Map(Lamp lamp) {
        BaseObj4Map lampDto;
        lampDto = new BaseObj4Map();
        lampDto.setLastUpdate(lamp.getLastUpdate());
        lampDto.setAlias(lamp.getAlias());
        lampDto.setGroup(lamp.getGroup() != null ? lamp.getGroup().getNameOfGroup() : "");
        lampDto.setName(lamp.getName());
        lampDto.setLocation(lamp.getLocation());
        WorkGroup4Map wg4m = new WorkGroup4Map();
        wg4m.setId(lamp.getWorkGroup().getId());
        wg4m.setName(lamp.getWorkGroup().getName());
        Groups4Map groups4Map = new Groups4Map();
        if (Objects.nonNull(lamp.getGroup())) {
            groups4Map.setId(lamp.getGroup().getId());
            groups4Map.setName(lamp.getGroup().getNameOfGroup());
            wg4m.setGroups(Collections.singleton(groups4Map));
        }
        lampDto.setWorkGroup(wg4m);
        Organization4Map o4m = new Organization4Map();
        o4m.setId(lamp.getOrganizationOwner().getId());
        o4m.setName(lamp.getOrganizationOwner().getName());
        lampDto.setOrganization(o4m);
        if (lamp.getCp_owner() != null) {
            lampDto.setCp_owner(lamp.getCp_owner().getId());
        }
        lampDto.setObjStates(Collections.emptyList());
        return lampDto;
    }


    /**
     * @param user
     * @param objInfo
     * @return
     * @throws ParseException
     */
    @Transactional
    @Override
    public BaseObj4Map addNewObj(User user, LampPropsMsg objInfo) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        if (lampRepository.findByName(objInfo.getEui()) == null) {
            Lamp lamp = new Lamp();
            lamp.setName(objInfo.getEui());
            lamp.setAlias(objInfo.getAlias());
            lamp.setLastUpdate(sdf.parse("1970-01-01T00:00:00Z"));
            lamp.setLocation(new Location(Double.parseDouble(objInfo.getLatitude()), Double.parseDouble(objInfo.getLongitude()), ""));
            lamp.setOrganizationOwner(user.getOrganizationOwner());
            lamp.setObjStates(Collections.emptyList());
            lamp.setDeleted(false);
            lamp.setWorkGroup(user.getWorkGroup());
            if (!StringUtils.isEmpty(objInfo.getCp())) {
                lamp.setCp_owner(controlPointsRepo.findByObjectName(objInfo.getCp()));
            }
            lampRepository.save(lamp);
            return convert2Obj4Map(lamp);
        }
        return null;

    }

    /**
     * @param ids список id
     * @return list ламп по id
     */
    @Override
    public List<Lamp> findLampByInId(List<Long> ids) {
        return lampRepository.findByArrId(ids);
    }


    /**
     * @param workGroup по какой группе
     * @return
     */
    @Override
    public List<ControlPoint> findCPWorkGroupWithPage(WorkGroup workGroup) {
        return controlPointsRepo.findByWorkGroupAndIsDeleted(workGroup, false);
    }

    // ToDo: перенести в кастомный репо
    @Override
    public List<Object[]> findAllLamp(WorkGroup group) {
        Query query = manager.createNativeQuery("SELECT id,object_name,alias,cp_id" + " FROM lamp where work_group_id=" + group.getId() + " AND is_deleted = false" + " ORDER BY id");
        List<Object[]> res = (List<Object[]>) query.getResultList();
        return res;
    }

    // ToDo: перенести в кастомный репо
    @Override
    public List<Object[]> findAllCP(WorkGroup group) {
        Query query = manager.createNativeQuery("SELECT id,object_name" + " FROM control_points where work_group_id=" + " AND is_deleted = false" + group.getId() + " ORDER BY id");
        List<Object[]> res = (List<Object[]>) query.getResultList();
        return res;
    }


    @Override
    public long countLampByWorkGroup(WorkGroup group, boolean deleted) {
        return lampRepository.countByWorkGroupAndIsDeleted(group, deleted);
    }

    /**
     * Изменить КП у Лампы
     *
     * @param lamp
     * @param controlPoint
     * @return DTO
     */
    @Transactional
    @Override
    public BaseObj4Map changeLampCP(Lamp lamp, ControlPoint controlPoint) {
        lamp.setCp_owner(controlPoint);
        lampRepository.save(lamp);
        getObj4Map(Collections.singletonList(lamp));
        return getObj4Map(Collections.singletonList(lamp)).get(0);

    }

    @Transactional
    @Override
    public Lamp getByEUI(String eui) {
        return lampRepository.findByName(eui);
    }

    /**
     * Обновление параметров Лампы
     *
     * @param properties
     * @return Lamp
     * @throws LampAlreadyExistException
     */
    @Transactional
    @Override
    public Lamp updateLamp(UpdateLampProp properties) throws LampAlreadyExistException {
        Lamp lamp = lampRepository.findByNameAndIsDeleted(properties.getEui(), false);
        if (Objects.nonNull(lamp)) {
            if (!properties.getEui().equals(properties.getNew_eui())) {
                Lamp byName = lampRepository.findByNameAndIsDeleted(properties.getNew_eui(), false);
                if (Objects.nonNull(byName)) throw new LampAlreadyExistException("дубликат eui");

            }
            if (Objects.nonNull(properties.getLat()) && Objects.nonNull(properties.getLon())) {
                Location loc = new Location();
                loc.setLatitude(properties.getLat());
                loc.setLongitude(properties.getLon());
                lamp.setLocation(loc);
            }

            lamp.setName(properties.getNew_eui());
            lamp.setAlias(properties.getAlias());
            final Lamp dbLamp = lampRepository.save(lamp);
            // обновление в Кеше
            Optional<LampByEui> cachedLamp = lampEuiRepo.findById(properties.getEui());
            if (cachedLamp.isPresent()) {
                LampByEui lampByEui = cachedLamp.get();
                Lamp currentLamp = lampByEui.getLamp();
                lampEuiRepo.delete(lampByEui);
                lampByEui.setEui(dbLamp.getName());
                lampByEui.setLamp(dbLamp);
                lampEuiRepo.save(lampByEui);
                Optional<LampsLastStates> lampsStates = lampsStatesRepo.findById(currentLamp);
                if (lampsStates.isPresent()) {
                    LampsLastStates states = lampsStates.get();
                    lampsStatesRepo.delete(states);
                    states.setLamp(dbLamp);
                    states.getStateObj().forEach(s -> s.setLamp(dbLamp));
                    lampsStatesRepo.save(states);
                }
            }
        }
        return lamp;
    }

    @Transactional
    @Override
    public BaseObj4Map addLamp(AddLampFromMap lamp, User user) throws LampAlreadyExistException, OrganizationNotFound {
        BaseObj4Map baseObj4Map = addLampFromMap(lamp, user);
        return baseObj4Map;

    }

    /**
     * Добавление новой Лампы из интерфейса карты с учетом Ролей юзеров
     *
     * @param lamp
     * @param user
     * @return DTO
     * @throws ControlPointNotFoundException
     * @throws WorkGroupNotFoundException
     * @throws LampAlreadyExistException
     */
    @Transactional
    public BaseObj4Map addLampFromMap(AddLampFromMap lamp, User user) throws ControlPointNotFoundException, WorkGroupNotFoundException, LampAlreadyExistException {
        Lamp new_lamp = new Lamp();
        ControlPoint cp = null;
        Organization organization = null;
        WorkGroup workGroup = null;
        if (Objects.nonNull(lampRepository.findByNameAndIsDeleted(lamp.getEui(), false))) return null;
        if (user.getRoles().contains(Role.ADMIN)) {
            if (Objects.nonNull(lamp.getOrganization()) && lamp.getOrganization() > 0) {
                Optional<Organization> organizationOpt = organizationRepo.findById(lamp.getOrganization());
                if (organizationOpt.isPresent()) {
                    organization = organizationOpt.get();
                } else throw new OrganizationNotFound("");
            }
            workGroup = workGroupRepo.findById(lamp.getWorkGroup()).orElseThrow(() -> new WorkGroupNotFoundException(""));
        } else organization = user.getOrganizationOwner();

        if (user.getRoles().contains(Role.SuperUserOwner)) {
            workGroup = workGroupRepo.findById(lamp.getWorkGroup()).orElseThrow(() -> new WorkGroupNotFoundException(""));
        } else if (user.getRoles().contains(Role.SuperUser)) workGroup = user.getWorkGroup();

        new_lamp.setWorkGroup(workGroup);

        new_lamp.setOrganizationOwner(organization);
        new_lamp.setName(lamp.getEui());
        new_lamp.setAlias(lamp.getAlias());
        if (Objects.nonNull(lamp.getCp()) && lamp.getCp() > 0) {
            cp = controlPointsRepo.findById(lamp.getCp()).orElseThrow(ControlPointNotFoundException::new);
        }
        new_lamp.setCp_owner(cp);
        Group group = groupRepository.findById(lamp.getGroup()).orElse(null);
        new_lamp.setGroup(group);
        Location location = new Location();
        location.setLatitude(lamp.getLat());
        location.setLongitude(lamp.getLon());
        new_lamp.setLocation(location);
        return convert2Obj4Map(lampRepository.save(new_lamp));

    }

    /**
     * ToDo: не используется?
     *
     * @param lamp
     * @param user
     * @return
     */
    @Deprecated
    @Transactional
    @Override
    public BaseObj4Map updateLamp(UpdateLampFromMap lamp, User user) {
        if (!StringUtils.isEmpty(lamp.getOld_eui()) && !(lamp.getOld_eui().equals(lamp.getEui()))) {
            if (Objects.nonNull(lampRepository.findByName((lamp.getEui())))) return null;
        }
        return updateLampFromMap(lamp, user);
    }

    @Transactional
    public BaseObj4Map updateLampFromMap(UpdateLampFromMap lamp, User user) throws ControlPointNotFoundException, WorkGroupNotFoundException, LampAlreadyExistException {

        ControlPoint cp = null;
        Organization organization = null;
        WorkGroup workGroup = null;
        Lamp lampFromDb;
        if (StringUtils.isEmpty(lamp.getOld_eui())) {
            lampFromDb = lampRepository.findByNameAndIsDeleted(lamp.getEui(), false);
        } else {
            lampFromDb = lampRepository.findByNameAndIsDeleted(lamp.getOld_eui(), false);
        }
        if (Objects.isNull(lampFromDb)) return null;
        if (user.getRoles().contains(Role.ADMIN)) {
            if (Objects.nonNull(lamp.getOrganization()) && lamp.getOrganization() > 0) {
                Optional<Organization> organizationOpt = organizationRepo.findById(lamp.getOrganization());
                if (organizationOpt.isPresent()) {
                    organization = organizationOpt.get();
                } else throw new OrganizationNotFound("");
            }
            workGroup = workGroupRepo.findById(lamp.getWorkGroup()).orElseThrow(() -> new WorkGroupNotFoundException(""));
        } else organization = user.getOrganizationOwner();

        if (user.getRoles().contains(Role.SuperUserOwner)) {
            workGroup = workGroupRepo.findById(lamp.getWorkGroup()).orElseThrow(() -> new WorkGroupNotFoundException(""));
        } else if (user.getRoles().contains(Role.SuperUser)) workGroup = user.getWorkGroup();

        lampFromDb.setWorkGroup(workGroup);
        lampFromDb.setName(lamp.getEui());
        lampFromDb.setOrganizationOwner(organization);
        lampFromDb.setAlias(lamp.getAlias());
        if (Objects.nonNull(lamp.getCp()) && lamp.getCp() > 0) {
            cp = controlPointsRepo.findById(lamp.getCp()).orElseThrow(ControlPointNotFoundException::new);
        }
        lampFromDb.setCp_owner(cp);
        Group group = groupRepository.findById(lamp.getGroup()).orElse(null);
        lampFromDb.setGroup(group);

        return convert2Obj4Map(lampRepository.save(lampFromDb));

    }


    /**
     * ToDo:в кастомный репозиторий! уже неиспользуется?
     *
     * @param eui
     */
    @Override
    public void deleteLamp(String eui) {
        if (pattern.matcher(eui).matches()) {
            Query nativeQuery = manager.createNativeQuery("UPDATE lamp SET is_deleted = true WHERE object_name = :eui")
                    .setParameter("eui", eui);
            nativeQuery.executeUpdate();
        }
    }


}
