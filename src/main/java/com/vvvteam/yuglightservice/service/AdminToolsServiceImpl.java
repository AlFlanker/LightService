package com.vvvteam.yuglightservice.service;

import com.vvvteam.yuglightservice.domain.DTO.BaseObjDTO;
import com.vvvteam.yuglightservice.domain.DTO.UserDTO;
import com.vvvteam.yuglightservice.domain.DTO.WorkGroupBaseObjResponse;
import com.vvvteam.yuglightservice.domain.DTO.WorkGroupResponse;
import com.vvvteam.yuglightservice.domain.DTO.WorkGroupUsersResponse;
import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.auth.Role;
import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.domain.entries.ControlPoint;
import com.vvvteam.yuglightservice.domain.entries.Group;
import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.entries.WorkGroup;
import com.vvvteam.yuglightservice.repositories.WorkGroupRepo;
import com.vvvteam.yuglightservice.service.interfaces.AdminToolsService;
import com.vvvteam.yuglightservice.service.interfaces.LampService;
import com.vvvteam.yuglightservice.service.interfaces.WorkGroupService;
import com.vvvteam.yuglightservice.service.request.and.response.ForAdminTools.SelectItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@RequiredArgsConstructor
@Service
public class AdminToolsServiceImpl implements AdminToolsService {
    private final UserService userService;
    private final WorkGroupRepo workGroupRepo;
    private final LampService objectsService;
    private final WorkGroupService workGroupService;
    private final EntityManager entityManager;


    /**
     * Запрос данных о рабочей группе.
     * @param selectItem параметризированный запрос
     * @param user
     * @return Лампы/ КП/ Юзеров
     * ToDo: на стратегию, методы getCPFromWG,getLampFromWG,getUserFromWG или все с учетом пагинацией или убрать (*подумать!)
     */
    @Override
    public ResponseEntity getObjectFromWorkGroup(SelectItem selectItem, User user) {
        switch (selectItem.getType()) {
            case CP:
                return getCPFromWG(selectItem, user);
            case Lamp:
                return getLampFromWG(selectItem, user);
            case Users:
                return getUserFromWG(selectItem, user);
            default:
                break;
        }
        return null;
    }

    @Transactional
    @Override
    public ResponseEntity<?> getWorkGroupResponce(Organization organization) {
        WorkGroupResponse workGroupResponse = new WorkGroupResponse();
        try {
            if (Objects.nonNull(organization)) {
                workGroupResponse.setGroupInfoList(workGroupService.getWorkGroupsInfo(organization));
                workGroupResponse.setStatus("ok");
                return ResponseEntity.ok(workGroupResponse);
            }
        } catch (LazyInitializationException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     *
     * @param selectItem
     * @param user
     * @return все КП из выбаранной рабочей группы
     *
     */
    private ResponseEntity<?> getCPFromWG(SelectItem selectItem, User user) {
        WorkGroup workGroup = getCurrentWorkGroup(selectItem, user);
        if (workGroup == null) return ResponseEntity.noContent().build();
        WorkGroupBaseObjResponse objResponse = new WorkGroupBaseObjResponse();
        objResponse.setStatus("ok");
        List<ControlPoint> controlPoints = objectsService.findCPWorkGroupWithPage(workGroup);
        List<BaseObjDTO> dtoList = controlPoints.stream()
                .map((l) -> new BaseObjDTO(l.getId(),
                        l.getObjectName(),
                        null,
                        null,
                        l.getLocation().getLatitude(),
                        l.getLocation().getLongitude()))
                .collect(Collectors.toList());
        objResponse.setData(dtoList);
        objResponse.setCurrentPage(0);
        objResponse.setTotalElem(controlPoints.size());
        objResponse.setTotalPage(1);
        return ResponseEntity.ok(objResponse);

    }

    /**
     *
     * @param selectItem
     * @param user
     * @return все пользователи из выбранной рабочей группы
     */
    private ResponseEntity<?> getUserFromWG(SelectItem selectItem, User user) {
        WorkGroup workGroup = getCurrentWorkGroup(selectItem, user);
        if (workGroup == null) return ResponseEntity.noContent().build();
        WorkGroupUsersResponse workGroupUsersResponse = new WorkGroupUsersResponse();
        workGroupUsersResponse.setStatus("ok");

        List<User> users = userService.loadUsersByWG(workGroup);
        workGroupUsersResponse.setData(users
                .stream()
                .map((User u) -> new UserDTO(u.getId(), u.getUsername(), u.getEmail(), u.getGroups()
                        .stream()
                        .map(Group::getNameOfGroup)
                        .collect(Collectors.toList()),(Role)u.getRoles().toArray()[0])).collect(Collectors.toList()));
        return ResponseEntity.ok(workGroupUsersResponse);
    }

    /**
     *
     * @param selectItem
     * @param user
     * @return все лампы из выбранной рабочей группы
     */
    private ResponseEntity<?> getLampFromWG(SelectItem selectItem, User user) {
        WorkGroup workGroup = getCurrentWorkGroup(selectItem, user);
        if (Objects.isNull(workGroup)) return ResponseEntity.noContent().build();
        WorkGroupBaseObjResponse objResponse = new WorkGroupBaseObjResponse();
        objResponse.setStatus("ok");
        List<Lamp> allLamp = objectsService.getAllLampByWorkGroup(workGroup);
        List<BaseObjDTO> dtoList = allLamp
                .stream()
                .map((l) -> new BaseObjDTO(l.getId(),
                        l.getName(),
                        l.getAlias(),
                        (Objects.nonNull(l.getGroup())) ? l.getGroup().getNameOfGroup() : "",
                        l.getLocation().getLatitude(),
                        l.getLocation().getLongitude()))
                .collect(Collectors.toList());
        objResponse.setData(dtoList);
        return ResponseEntity.ok(objResponse);
    }

    private WorkGroup getCurrentWorkGroup(SelectItem selectItem, User user) {
        WorkGroup workGroup = new WorkGroup();
        if (Objects.isNull(selectItem.getId())) {
            workGroup = user.getWorkGroup();
        } else {
            Optional<WorkGroup> inDB = workGroupRepo.findById(selectItem.getId());
            if (inDB.isPresent()) {
                workGroup = inDB.get();
            }
        }
        return workGroup;
    }

    // ToDo: вынести deleteWorkGroup,deleteLamp,deleteCP в Кастомный репозиторий
    /**
     *
     * @param group
     */
    @Transactional
    @Override
    public void deleteWorkGroup(WorkGroup group) {
            //помечаем группы пользователей из группы
        Query nativeQuery = entityManager.createNativeQuery("UPDATE groups SET is_deleted = true WHERE user_id IN ( SELECT id FROM usr WHERE work_group_id = :group_id )")
                .setParameter("group_id",group.getId());
        nativeQuery.executeUpdate();
        //помечаем пользователей из группы и делаем их неактивными
        nativeQuery = entityManager.createNativeQuery("UPDATE usr SET is_deleted = true, active = false WHERE work_group_id = :group_id")
                .setParameter("group_id",group.getId());
        nativeQuery.executeUpdate();
        //помечаем лампы из группы
        nativeQuery = entityManager.createNativeQuery("UPDATE lamp SET is_deleted = true WHERE work_group_id = :group_id")
                .setParameter("group_id",group.getId());
        nativeQuery.executeUpdate();
        //помечаем состояния ламп группы
        nativeQuery = entityManager.createNativeQuery("UPDATE states SET is_deleted = true WHERE lamp_id IN (SELECT lamp.id FROM lamp WHERE lamp.work_group_id = :group_id)")
                .setParameter("group_id",group.getId());
        nativeQuery.executeUpdate();
        //помечаем КП из группы
        nativeQuery = entityManager.createNativeQuery("UPDATE control_points SET is_deleted = true WHERE work_group_id = :group_id ")
                .setParameter("group_id",group.getId());
        nativeQuery.executeUpdate();
        //помечаем группe
        nativeQuery = entityManager.createNativeQuery("UPDATE work_group SET is_deleted = true WHERE id = :group_id ")
                .setParameter("group_id",group.getId());
        nativeQuery.executeUpdate();
    }

    @Transactional
    @Override
    public void deleteLamp(Lamp lamp) {
        Query nativeQuery = entityManager.createNativeQuery("UPDATE lamp SET is_deleted = true WHERE id = :id")
                .setParameter("id",lamp.getId());
        nativeQuery.executeUpdate();
    }

    @Transactional
    @Override
    public void deleteCP(ControlPoint cp) {
        Query nativeQuery = entityManager.createNativeQuery("UPDATE control_points SET is_deleted = true WHERE id = :id")
                .setParameter("id",cp.getId());
        nativeQuery.executeUpdate();
    }
}
