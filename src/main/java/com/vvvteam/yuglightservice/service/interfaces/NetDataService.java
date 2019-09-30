package com.vvvteam.yuglightservice.service.interfaces;

import com.vvvteam.yuglightservice.domain.auth.User;
import com.vvvteam.yuglightservice.service.request.and.response.VegaCMD.requests.VegaDataRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.CompletableFuture;

public interface NetDataService {
    CompletableFuture<String> findData(UriComponentsBuilder builder) throws InterruptedException;
    CompletableFuture<String>  sendCommand(UriComponentsBuilder builder, String param);

    void SendCommandVega(User user, VegaDataRequest txRequest);



}
