package com.vvvTeam.yuglightservice.service.analizeDataServ;

import com.vvvTeam.yuglightservice.domain.auth.Organization;
import com.vvvTeam.yuglightservice.domain.entries.DataFromNet868;
import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import com.vvvTeam.yuglightservice.domain.entries.WorkGroup;
import com.vvvTeam.yuglightservice.repositories.LampRepository;
import com.vvvTeam.yuglightservice.repositories.StatesRepository;
import com.vvvTeam.yuglightservice.repositories.support.LampEuiRepo;
import com.vvvTeam.yuglightservice.repositories.support.LampsStatesRepo;
import com.vvvTeam.yuglightservice.service.AnalizeDataServiceImpl;
import com.vvvTeam.yuglightservice.service.interfaces.AnalizeDataService;
import com.vvvTeam.yuglightservice.service.request.and.response.VegaCMD.response.VegaRxDataResp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.function.UnaryOperator;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class AnalizeDataServiceTest {

    @Autowired
    private ApplicationEventPublisher publisher;
    @MockBean
    LampRepository lampRepository;
    @MockBean
    StatesRepository statesRepository;
    @MockBean
    LampsStatesRepo lampsStatesRepo;
    @MockBean
    LampEuiRepo lampEuiRepo;

    @MockBean
    ApplicationEventPublisher applicationEventPublisher;
    @Captor
    protected ArgumentCaptor<Object> publishEventCaptor;

    private UnaryOperator<String> convert = o -> {
        int length = o.length();
        StringBuilder sb = new StringBuilder();
        sb.append(o, 0, 2);
        for (int i = 2; i < length; i += 2) {
            sb.append("-").append(o, i, i + 2);
        }
        return sb.toString();
    };

    Lamp getTestLamp(String testEUI) {
        Lamp lamp = new Lamp();
        lamp.setId(1l);
        lamp.setName(convert.apply(testEUI));
        lamp.setAlias("LC503E1_54107");
        WorkGroup workGroup = new WorkGroup();
        workGroup.setId(1l);
        lamp.setWorkGroup(workGroup);
        Organization organization = new Organization();
        organization.setId(1l);
        lamp.setOrganizationOwner(organization);
        return lamp;
    }

    @DisplayName("Тестирование добавления новых данных от Vega server \n Позитивный сценарий")
    @Test
    public void updateStatesTest() throws Exception {
        VegaRxDataResp testData = new VegaRxDataResp();
        String testEUI = "74E14A4F08586669";
        Lamp lamp = getTestLamp(testEUI);
        AnalizeDataService dataService = new AnalizeDataServiceImpl(publisher, lampRepository, statesRepository, lampsStatesRepo, lampEuiRepo);
        testData.setDevEui(testEUI);
        testData.setData("E5-00-B7-01-28-00-37-0D-B0-49-34-42-7C-00-1C-42-00-40".replace("-", ""));
        testData.setTs(new Date());
        Mockito.doReturn(lamp)
                .when(lampRepository)
                .findByNameAndIsDeleted(convert.apply(testEUI), false);
        Mockito.doReturn(lamp)
                .when(lampRepository)
                .save(lamp);
        Mockito.doReturn(Optional.ofNullable(null))
                .when(lampsStatesRepo)
                .findById(lamp);

        dataService.updateStates(testData);

        Mockito.verify(lampsStatesRepo, Mockito
                .times(1))
                .save(ArgumentMatchers.any());

    }

    @DisplayName("Тестирование добавления новых данных от Vega server \n Негативный сценарий")
    @Test
    public void updateStatesTestNegative() {
        VegaRxDataResp testData = new VegaRxDataResp();
        String testEUI = "74E14A4F08586669";
        Lamp lamp = new Lamp();
        lamp.setName(convert.apply(testEUI));
        lamp.setAlias("LC503E1_54107");

        testData.setDevEui(testEUI + '1');
        testData.setData("E5-00-B7-01-28-00-37-0D-B0-49-34-42-7C-00-1C-42-00-40".replace("-", ""));
        testData.setTs(new Date());

        Mockito.doReturn(lamp)
                .when(lampRepository)
                .findByName(convert.apply(testEUI));
        Mockito.doReturn(lamp)
                .when(lampRepository)
                .save(lamp);
        AnalizeDataService dataService = new AnalizeDataServiceImpl(publisher, lampRepository, statesRepository, lampsStatesRepo, lampEuiRepo);

        Mockito.verify(lampEuiRepo, Mockito.times(0)).findById(ArgumentMatchers.anyString());
    }

    private List<Lamp> getLamps() {
        Lamp lamp;
        List<Lamp> lamplist = new ArrayList<>();
        WorkGroup workGroup = new WorkGroup();
        workGroup.setId(1l);
        Organization organization = new Organization();
        organization.setId(1l);

        for (int i = 0; i < 10; i++) {
            lamp = new Lamp();
            lamp.setAlias("LC503E1_" + i);
            lamp.setName("74-E1-4A-4F-08-58-66-0".toLowerCase() + i);
            lamp.setObjStates(Collections.emptyList());
            lamplist.add(lamp);
            lamp.setOrganizationOwner(organization);
            lamp.setWorkGroup(workGroup);
        }
        return lamplist;
    }

    private List<DataFromNet868> getNet868Data() {
        DataFromNet868 data;
        List<DataFromNet868> dataFromNet868s = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data = new DataFromNet868();
            data.setDeviceEui("74-E1-4A-4F-08-58-66-0".toLowerCase() + i);
            data.setData("E5-00-B7-01-28-00-37-0D-B0-49-34-42-7C-00-1C-42-00-40".replace("-", ""));
            data.setTime(new Date());
            dataFromNet868s.add(data);
        }
        return dataFromNet868s;

    }

    @DisplayName("Тестирование добавления новых данных от Net868 server \n Позитивный сценарий")
    @Test
    public void addNewDataNet868Test() throws Exception {
        List<Lamp> lamp = new ArrayList<>();
        AnalizeDataService dataService = new AnalizeDataServiceImpl(publisher, lampRepository, statesRepository, lampsStatesRepo, lampEuiRepo);
        Organization organization = new Organization();
        Mockito.doReturn(getLamps())
                .when(lampRepository)
                .findByOrganizationOwnerAndIsDeleted(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());

        Mockito.when(lampRepository.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<DataFromNet868> net868Data = getNet868Data();

        dataService.addNewDataNet868(net868Data, organization);

        Mockito.verify(statesRepository, Mockito.times(10)).saveAll(Mockito.any());

    }

    @DisplayName("Тестирование добавления новых данных от Net868 server \n Негативный сценарий")
    @Test
    public void addNewDataNet868NegativeTest() throws Exception {
        List<Lamp> lamp = new ArrayList<>();
        Organization organization = new Organization();
        AnalizeDataService dataService = new AnalizeDataServiceImpl(publisher, lampRepository, statesRepository, lampsStatesRepo, lampEuiRepo);
        Mockito.doReturn(getLamps())
                .when(lampRepository)
                .findByOrganizationOwner(ArgumentMatchers.any());
        Mockito.when(lampRepository.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        List<DataFromNet868> net868Data = getNet868Data();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            dataService.addNewDataNet868(null, organization);
        }, "List<DataFromNet868> cannot be null");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            dataService.addNewDataNet868(net868Data, null);
        }, "organization cannot be null");


    }
}
