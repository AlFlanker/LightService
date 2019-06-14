package com.vvvTeam.yuglightservice.service.request.and.response.Responses;

import com.vvvTeam.yuglightservice.domain.auth.TypeOfService;
import lombok.Data;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ResponceNetData {
     private String token;
     private String login;
     private String password;
     private TypeOfService typeOfService;
     private boolean isActive;
     private String url;
     private String url2;
     private String wss;

     public ResponceNetData(String token, String login, String password, TypeOfService typeOfService,boolean isActive,String url,String url2,String wss) {
          this.token = token;
          this.login = login;
          this.password = password;
          this.typeOfService = typeOfService;
          this.isActive = isActive;
          this.url=url;
          this.url2=url2;
          this.wss = wss;
     }
}
