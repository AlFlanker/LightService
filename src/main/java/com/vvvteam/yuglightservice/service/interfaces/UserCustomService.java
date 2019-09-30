package com.vvvteam.yuglightservice.service.interfaces;

import com.vvvteam.yuglightservice.domain.DTO.UserEdit;
import com.vvvteam.yuglightservice.domain.auth.User;

public interface UserCustomService {
    User changeUserData(User user, UserEdit data);
}
