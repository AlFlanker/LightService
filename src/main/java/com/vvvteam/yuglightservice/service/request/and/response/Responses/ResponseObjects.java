package com.vvvteam.yuglightservice.service.request.and.response.Responses;


import com.vvvteam.yuglightservice.domain.auth.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseObjects {
    String msg;
    User result;
}
