package com.vvvteam.yuglightservice.repositories.customsInterface;

import java.util.List;
import java.util.Set;

public interface LampCustomRepository {
    List<String> getExistLamp(Set<String> lamps,boolean deleted);
}
