package com.vvvteam.yuglightservice.repositories;

import com.vvvteam.yuglightservice.domain.entries.Lamp;
import com.vvvteam.yuglightservice.domain.entries.StateObj;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface StatesRepository extends JpaRepository<StateObj, Long> {
    /*Add SortRequests!!*/
    List<StateObj> findByLamp(Lamp lamp);

    List<StateObj> findByPassed(Boolean passed);

    StateObj findByIdAndPassed(Long id, boolean passed);

    List<StateObj> findByLampAndPassed(Lamp lamp, boolean isPassed);

    @Query("SELECT s FROM StateObj s where s.lamp.id = :lamp and s.passed=:isPassed order by s.currentDate desc")
    List<StateObj> finStatesByLampAndPassed(@Param("lamp") long lamp, @Param("isPassed") boolean isPassed,Pageable page);

    @Query("SELECT s FROM StateObj s where s.lamp.id = :lamp order by s.currentDate desc")
    List<StateObj> finStatesByLamp(@Param("lamp") long lamp,Pageable page);

    @Query("SELECT s FROM StateObj s where s.lamp.id = :lamp and s.currentDate between :start and :stop order by s.currentDate desc ")
    List<StateObj> findByLampAndCurrentDateBetween(@Param("lamp") long lamp,@Param("start")Date start,@Param("stop")Date stop,Pageable pageable);







}
