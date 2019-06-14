package com.vvvTeam.yuglightservice.service.interfaces;

import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.requests.VegaDataRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.CompletableFuture;

public interface NetDataService {
    CompletableFuture<String> findData(UriComponentsBuilder builder) throws InterruptedException;
    CompletableFuture<String>  sendCommand(UriComponentsBuilder builder, String param);

    void SendCommandVega(User user, VegaDataRequest txRequest);



}
