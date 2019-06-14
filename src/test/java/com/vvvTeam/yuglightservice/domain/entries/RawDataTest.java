package com.vvvTeam.yuglightservice.domain.entries;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@ExtendWith(SpringExtension.class)
class RawDataTest {
    private String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, numchars);
    }
    @Test
    void decodeByteArrayToDevice() {
        String hexString = getRandomHexString(36);
        RawData data = RawData.decodeByteArrayToDevice("e10075012a00390de14834429cfd1b420040");
        RawData data1= RawData.decodeByteArrayToDevice("e70099011b003b0dff483442a7fd1b422801");
        System.out.println("e10075012a00390de14834429cfd1b420040".length());
        assertNotNull(data);
    }
    @Test
    void ran() {

    }

}