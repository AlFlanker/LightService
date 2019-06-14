package com.vvvTeam.yuglightservice.service.interfaces;

import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.domain.entries.ControlPoint;
import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import com.vvvTeam.yuglightservice.domain.entries.WorkGroup;
import com.vvvTeam.yuglightservice.service.request.and.response.ForAdminTools.SelectItem;
import org.springframework.http.ResponseEntity;

public interface AdminToolsService {
    ResponseEntity<?> getObjectFromWorkGroup(SelectItem selectItem, User user);
    ResponseEntity<?> getWorkGroupResponce(Organization organization);
    void deleteWorkGroup(WorkGroup group);
    void deleteLamp(Lamp lamp);
    void deleteCP(ControlPoint cp);


}
