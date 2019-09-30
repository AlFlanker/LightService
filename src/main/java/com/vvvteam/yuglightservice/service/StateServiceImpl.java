package com.vvvteam.yuglightservice.service;

import com.vvvteam.yuglightservice.domain.auth.NetData;
import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.entries.StateObj;
import com.vvvteam.yuglightservice.domain.support.LampsLastStates;
import com.vvvteam.yuglightservice.repositories.StatesRepository;
import com.vvvteam.yuglightservice.repositories.support.LampsStatesRepo;
import com.vvvteam.yuglightservice.service.interfaces.StateService;
import com.vvvteam.yuglightservice.service.request.and.response.MapAPI.Request.Support.PassedObj;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class StateServiceImpl implements StateService {
    private final StatesRepository statesRepository;
    private final EntityManager manager;
    private final LampsStatesRepo lampsStatesRepo;

    @Override
    @Transactional
    public List<StateObj> getNotPassedStates(Lamp obj) {
        Optional<LampsLastStates> lastStates = lampsStatesRepo.findById(obj);
        List<StateObj> result;
        if (lastStates.isPresent()) {
            result = lastStates.get().getStateObj();
            result.sort(Comparator.comparing(StateObj::getCurrentDate).reversed());
            result = Collections.singletonList(result.get(0));
        } else {
            result = statesRepository.finStatesByLamp(obj.getId(), PageRequest.of(0, 1));
        }
        return result != null ? result : Collections.emptyList();
    }

    @Override
    @Transactional
    public void Passed(List<PassedObj> data) {
        // ToDo: переделать логику работу
    }


    /**
     * @param lamp
     * @param start
     * @param stop
     * @param tags  названия полей, которые надо вернуть
     * @param limit max entries
     * @return list of states
     */
    @SuppressWarnings("unchecked")
    @Cacheable("states")
    @Override
    public List<Object[]> getStatesBetween(Lamp lamp, Date start, Date stop, long limit, Set<String> tags) {
        String param = String.join(", ", tags);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Query query = manager.createNativeQuery("SELECT " + param + " FROM states where lamp_id=" + lamp.getId() + " AND date_of_changed BETWEEN '" + format.format(start) + "' and '" + format.format(stop) + "' ORDER BY date_of_changed " + " LIMIT " + limit);
        List<Object[]> res = (List<Object[]>) query.getResultList();
        return res;
    }

    /**
     * @param lamp
     * @param start
     * @param stop
     * @param limit
     * @return list of states
     */
    @Override
    public List<StateObj> getStatesBetween(Lamp lamp, Date start, Date stop, int limit) {
        return Optional.of(statesRepository.findByLampAndCurrentDateBetween(lamp.getId(), start, stop, PageRequest.of(0, limit)))
                .orElse(Collections.emptyList());

    }


    private UriComponentsBuilder getRequestBody(Lamp lamp, String url, NetData netData) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((Objects.isNull(lamp.getLastUpdate())) ? new Date() : lamp.getLastUpdate());
        calendar.add(Calendar.HOUR, -3);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        builder.queryParam("token", netData.getToken());
        builder.queryParam("deviceEui", lamp.getName().toLowerCase());
        builder.queryParam("count", 10);
        builder.queryParam("offset", 0);
        builder.queryParam("startDate", simpleDateFormat.format(calendar.getTime()));
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, -3);
        builder.queryParam("endDate", simpleDateFormat.format(calendar.getTime()));
        builder.queryParam("order", "desc");
        return builder;
    }


}
