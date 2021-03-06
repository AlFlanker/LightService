package com.vvvteam.yuglightservice.service.interfaces;

import com.vvvteam.yuglightservice.domain.auth.Organization;
import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.domain.entries.ControlPoint;
import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.entries.WorkGroup;
import com.vvvteam.yuglightservice.service.request.and.response.ForAdminTools.SelectItem;
import org.springframework.http.ResponseEntity;

public interface AdminToolsService {
    ResponseEntity<?> getObjectFromWorkGroup(SelectItem selectItem, User user);
    ResponseEntity<?> getWorkGroupResponce(Organization organization);
    void deleteWorkGroup(WorkGroup group);
    void deleteLamp(Lamp lamp);
    void deleteCP(ControlPoint cp);


}
