package com.vvvteam.yuglightservice.service.StatesServ;

import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.entries.RawData;
import com.vvvteam.yuglightservice.domain.entries.StateObj;
import com.vvvteam.yuglightservice.repositories.LampRepository;
import com.vvvteam.yuglightservice.repositories.StatesRepository;
import com.vvvteam.yuglightservice.service.interfaces.StateService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Необходимо сгенирировать состояния за 2018 год -> @BeforeEach
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Transactional

@Sql(value={"/create-lamp-before.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class StateServiceTest {
    @Autowired
    private MockMvc mockMvc;

    private Supplier<Integer> getRandMouth = ()-> ThreadLocalRandom.current().nextInt(0, 11);
    private Supplier<Integer> getRandDay = ()-> ThreadLocalRandom.current().nextInt(1, 29);
    private Supplier<Integer> getRandHours = ()-> ThreadLocalRandom.current().nextInt(1, 22);
    private Supplier<Integer> getRandMinutes = ()-> ThreadLocalRandom.current().nextInt(0, 59);

    private Supplier<Integer> getRand_iAc = ()-> ThreadLocalRandom.current().nextInt(300, 500);
    private Supplier<Integer> getRand_vAc = ()-> ThreadLocalRandom.current().nextInt(190, 230);
    private Supplier<Integer> getRand_temp = ()-> ThreadLocalRandom.current().nextInt(30, 50);
    private Supplier<Integer> getRand_vDCBoard = ()-> ThreadLocalRandom.current().nextInt(3200, 3500);
    private Supplier<Byte> getRand_brightness = ()-> (byte)ThreadLocalRandom.current().nextInt(0, 100);
    private Supplier<Byte> getRand_state = ()-> (byte)ThreadLocalRandom.current().nextInt(0, 100);



    @Autowired
    private StatesRepository statesRepository;

    @Autowired
    private LampRepository lampRepository;

    @Autowired
    private StateService service;

    private Date getRandomDate(int mouth){
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.YEAR,2018);
        gc.set(Calendar.MONTH, mouth);
        gc.set(Calendar.DAY_OF_MONTH, getRandDay.get());
        gc.set(Calendar.HOUR_OF_DAY, getRandHours.get());
        gc.set(Calendar.MINUTE, 30);
        gc.set(Calendar.SECOND, 30);
        return gc.getTime();
    }

    private void generateFixStates( Lamp lamp){
        StateObj obj;
        RawData data;
        long i=0;
        long id = 1;
        long count = 100;
        List<StateObj> list = new ArrayList<>();
        for (int j = 0; j < 12; j++) {
            for (; i < count; i++) {
                obj = new StateObj();
                data = new RawData(getRand_vAc.get(), getRand_iAc.get(), getRand_temp.get(), getRand_vDCBoard.get(), (float) 1.1, (float) 1.1, getRand_brightness.get(), getRand_state.get());
                obj.setId(id++);
                obj.setCurrentDate(getRandomDate(j));
                obj.setAck(false);
                obj.setPassed(false);
                obj.setDeleted(false);
                obj.setData(data);
                obj.setLamp(lamp);
                list.add(obj);
            }
           i=0;
        }
        statesRepository.saveAll(list);
    }


    @BeforeEach
    public void beforeTest() throws Exception {
        Optional<Lamp> lamp = lampRepository.findById(1l);
        generateFixStates(lamp.get());
        List<StateObj> all = statesRepository.findAll();
        System.out.println(all.size());
    }

    /**
     * Если падает, то вероятнее тестовые данные надо сравнить и запросом проверить количествол
     * @throws Exception
     */
    @DisplayName("Проверка выборки за месяц")
    @Test
    public void test1()throws Exception{
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.YEAR,2018);
        gc.set(Calendar.MONTH, 1);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        Date start = gc.getTime();
        gc.set(Calendar.YEAR,2018);
        gc.set(Calendar.MONTH, 2);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        Date stop = gc.getTime();
        Optional<Lamp> lamp = lampRepository.findById(1l);
        List<StateObj> byLampAndPassed = statesRepository.findByLampAndPassed(lamp.get(), false);
        List<StateObj> stateObjs = service.getStatesBetween(lamp.get(), start, stop,1001);
        Assertions.assertEquals(100,stateObjs.size());

    }
    @DisplayName("Проверка выборки за 2 месяца")
    @Test
    public void test2()throws Exception{
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.YEAR,2018);
        gc.set(Calendar.MONTH, 0);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        Date start = gc.getTime();
        gc.set(Calendar.YEAR,2018);
        gc.set(Calendar.MONTH, 2);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        Date stop = gc.getTime();
        Optional<Lamp> lamp = lampRepository.findById(1l);
        List<StateObj> byLampAndPassed = statesRepository.findByLampAndPassed(lamp.get(), false);
        List<StateObj> stateObjs = service.getStatesBetween(lamp.get(), start, stop,1001);
        Assertions.assertEquals(200l,stateObjs.size());

    }

    @DisplayName("Проверка лимита")
    @Test
    public void test3()throws Exception{
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.YEAR,2018);
        gc.set(Calendar.MONTH, 0);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        Date start = gc.getTime();
        gc.set(Calendar.YEAR,2018);
        gc.set(Calendar.MONTH, 11);
        gc.set(Calendar.DAY_OF_MONTH, 20);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        Date stop = gc.getTime();
        Optional<Lamp> lamp = lampRepository.findById(1l);
        List<StateObj> byLampAndPassed = statesRepository.findByLampAndPassed(lamp.get(), false);
        List<StateObj> stateObjs = service.getStatesBetween(lamp.get(), start, stop,10);
        Assertions.assertEquals(10,stateObjs.size());


    }




}
