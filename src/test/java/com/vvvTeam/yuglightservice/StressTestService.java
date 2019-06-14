package com.vvvTeam.yuglightservice;

import com.vvvTeam.yuglightservice.domain.entries.*;
import com.vvvTeam.yuglightservice.repositories.LampRepository;
import com.vvvTeam.yuglightservice.repositories.OrganizationRepo;
import com.vvvTeam.yuglightservice.repositories.StatesRepository;
import com.vvvTeam.yuglightservice.repositories.WorkGroupRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
@Slf4j
/**
 * @return Очень много случайных состояний для существущих(!!!!) ласп
 * @see StressTestService.generateFixStates
 *
 */
//@Component
public class StressTestService implements CommandLineRunner {

    private final StatesRepository statesRepository;

    private final LampRepository lampRepository;

    private final OrganizationRepo organizationRepo;

    private final WorkGroupRepo groupRepo;

    @Autowired
    public StressTestService(StatesRepository statesRepository, LampRepository lampRepository, OrganizationRepo organizationRepo, WorkGroupRepo groupRepo) {
        this.statesRepository = statesRepository;
        this.lampRepository = lampRepository;
        this.organizationRepo = organizationRepo;
        this.groupRepo = groupRepo;
    }

    private Supplier<Integer> getRandMouth = ()-> ThreadLocalRandom.current().nextInt(0, 11);
    private Supplier<Integer> getRandDay = ()-> ThreadLocalRandom.current().nextInt(0, 29);
    private Supplier<Integer> getRandHours = ()-> ThreadLocalRandom.current().nextInt(0, 23);
    private Supplier<Integer> getRandMinutes = ()-> ThreadLocalRandom.current().nextInt(0, 59);
    private Supplier<Integer> getRand_iAc = ()-> ThreadLocalRandom.current().nextInt(300, 500);
    private Supplier<Integer> getRand_vAc = ()-> ThreadLocalRandom.current().nextInt(190, 230);
    private Supplier<Integer> getRand_temp = ()-> ThreadLocalRandom.current().nextInt(30, 50);
    private Supplier<Integer> getRand_vDCBoard = ()-> ThreadLocalRandom.current().nextInt(3200, 3500);
    private Supplier<Byte> getRand_brightness = ()-> (byte)ThreadLocalRandom.current().nextInt(0, 100);
    private Supplier<Byte> getRand_state = ()-> (byte)ThreadLocalRandom.current().nextInt(0, 100);

    private Date getRandomDate(int mouth,int year){
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.YEAR,year);
        gc.set(Calendar.MONTH, mouth);
        gc.set(Calendar.DAY_OF_MONTH, getRandDay.get());
        gc.set(Calendar.HOUR_OF_DAY, getRandHours.get());
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        return gc.getTime();
    }

    /**
     *
     * @param lamps существующие лампы!
     * @param quantity записей на лампу
     */
    private void generateFixStates( List<Lamp> lamps,long quantity,int year){
        StateObj obj;
        RawData data;
        long i=0;
        long offset= 3091190;


        long count = quantity;
        log.debug("generateFixStates");
        List<StateObj> list = new ArrayList<>();
        for(Lamp lamp:lamps) {
            for (int j = 0; j < 11; j++) {
                for (; i < count + 1; i++) {
                    obj = new StateObj();
                    data = new RawData(getRand_vAc.get(), getRand_iAc.get(), getRand_temp.get(), getRand_vDCBoard.get(), (float) 1.1, (float) 1.1, getRand_brightness.get(), getRand_state.get());
                    obj.setId( i+ offset+1);
                    obj.setCurrentDate(getRandomDate(j,year));
                    obj.setAck(false);
                    obj.setPassed(false);
                    obj.setDeleted(false);
                    obj.setData(data);
                    obj.setLamp(lamp);
                    list.add(obj);
                }
                count += quantity;
                log.debug("add " + quantity + " states");
            }
        }
        log.debug("add " + 12*quantity + " states");
        statesRepository.saveAll(list);
    }

    @Override
    public void run(String... args) throws Exception {
        log.debug("Check quentity of states");
        if((statesRepository.findAll(PageRequest.of(0,10))).getTotalElements()>=1l) {
            log.debug("start generate data");
            generateFixStates((List<Lamp>) lampRepository.findAll(), 1000l,2018);
            log.debug("2018 year");
        }
        log.debug("End of task");

    }
}
