package com.vvvTeam.yuglightservice.service.interfaces;

import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.entries.DataFromNet868;
import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import com.vvvTeam.yuglightservice.domain.entries.StateObj;
import com.vvvTeam.yuglightservice.service.request.and.response.Responses.BaseObj4Map;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.response.VegaRxDataResp;

import java.util.List;

public interface AnalizeDataService {
    void updateStates(VegaRxDataResp dataResp);
    int addNewDataNet868(List<DataFromNet868> list, Organization organization);
    BaseObj4Map getObj4MapWithState(Lamp lamp, StateObj state);

}
