package com.vvvTeam.yuglightservice.service.request.and.response.Responses;


import com.vvvTeam.yuglightservice.domain.auth.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseObjects {
    String msg;
    User result;
}
