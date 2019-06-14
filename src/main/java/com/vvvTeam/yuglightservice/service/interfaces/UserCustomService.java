package com.vvvTeam.yuglightservice.service.interfaces;

import com.vvvTeam.yuglightservice.domain.DTO.UserEdit;
import com.vvvTeam.yuglightservice.domain.auth.Role;
import com.vvvTeam.yuglightservice.domain.auth.User;

public interface UserCustomService {
    User changeUserData(User user, UserEdit data);
}
