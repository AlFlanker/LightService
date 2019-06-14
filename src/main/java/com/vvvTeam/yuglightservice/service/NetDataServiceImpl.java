package com.vvvTeam.yuglightservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvvTeam.yuglightservice.domain.auth.User;
import com.vvvTeam.yuglightservice.domain.auth.Vega.UserSessionData;
import com.vvvTeam.yuglightservice.service.interfaces.NetDataService;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.requests.VegaDataRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class NetDataServiceImpl implements NetDataService {
    private final UserSessionData sessionData;
    private final RestTemplate restTemplate;

    /**
     * запрос данных от Net868
     * @param builder
     * @return
     * @throws InterruptedException
     */
    @Async
    @Override
    public CompletableFuture<String> findData(UriComponentsBuilder builder) throws InterruptedException {
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        log.info("netdataService",Thread.currentThread().getName());
        String entry = restTemplate.getForObject(builder.build().encode().toUri(),String.class);
        log.info("netdataService",entry);
        return CompletableFuture.completedFuture(entry);
    }

    /**
     * Отправка команд Net868
     * @param builder
     * @param param
     * @return код ответа от сервера
     */
    @Async
    @Override
    public CompletableFuture<String>  sendCommand(UriComponentsBuilder builder,String param){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(param,httpHeaders);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        log.info(builder.build().toString());
        log.info(param);
        ResponseEntity<String> res = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST,entity,String.class);
        log.info(res.getStatusCode().toString());
        return CompletableFuture.completedFuture(res.getStatusCode().toString());
    }

    /**
     * Отправка команд Vega серверу через WebSocket
     * @param user
     * @param txRequest
     */
    @Async
    @Override
    public void SendCommandVega(User user, VegaDataRequest txRequest)  {
        ObjectMapper mapper = new ObjectMapper();
        WebSocketSession session = sessionData.getSessions().get(user.getOrganizationOwner().getId());
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(txRequest)));
            log.info(mapper.writeValueAsString(txRequest));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
