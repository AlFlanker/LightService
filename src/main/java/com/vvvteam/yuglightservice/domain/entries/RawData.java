package com.vvvteam.yuglightservice.domain.entries;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Data
@Embeddable
@NoArgsConstructor
public class RawData implements Serializable {

    private int v_ac;
    private int i_ac;
    private int temperature;
    private int vdcboard;
    private float latitude;
    private float longitude;
    private byte brightness;
    private byte state;


    public static synchronized RawData decodeByteArrayToDevice(String rawStr)  {

        RawData rawData = new RawData();
        byte[] buf;
        if(rawStr.length()==36) {
            byte[] raw = hexStringToByteArray(rawStr);
            Field[] fields = RawData.class.getDeclaredFields();


            for (Field field : fields) {
                //name = field.getName();
                if ("v_ac".equals(field.getName())) {
                    buf = new byte[2];
                    System.arraycopy(raw, 0, buf, 0, 2);
                    rawData.setV_ac(ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getChar());
                }
                if ("i_ac".equals(field.getName())) {
                    buf = new byte[2];
                    System.arraycopy(raw, 2, buf, 0, 2);
                    rawData.setI_ac(ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getChar());
                }
                if ("temperature".equals(field.getName())) {
                    buf = new byte[2];
                    System.arraycopy(raw, 4, buf, 0, 2);
                    rawData.setTemperature(ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getChar());

                }
                if ("vdcboard".equals(field.getName())) {
                    buf = new byte[2];
                    System.arraycopy(raw, 6, buf, 0, 2);
                    rawData.setVdcboard(ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getChar());
                }
                if ("latitude".equals(field.getName())) {
                    buf = new byte[4];
                    System.arraycopy(raw, 8, buf, 0, 4);
                    rawData.setLatitude(ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getFloat());

                }
                if ("longitude".equals(field.getName())) {
                    buf = new byte[4];
                    System.arraycopy(raw, 12, buf, 0, 4);
                    rawData.setLongitude(ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getFloat());

                }
                if ("brightness".equals(field.getName())) {
                    buf = new byte[1];
                    System.arraycopy(raw, 16, buf, 0, 1);
                    rawData.setBrightness(ByteBuffer.wrap(buf).get());
                }
                if ("state".equals(field.getName())) {
                    buf = new byte[1];
                    System.arraycopy(raw, 17, buf, 0, 1);
                    rawData.setState(ByteBuffer.wrap(buf).get());
                }

            }
        }

        return rawData;
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public RawData(int v_ac, int i_ac, int temperature, int vdcboard, float latitude, float longitude, byte brightness, byte state) {
        this.v_ac = v_ac;
        this.i_ac = i_ac;
        this.temperature = temperature;
        this.vdcboard = vdcboard;
        this.latitude = latitude;
        this.longitude = longitude;
        this.brightness = brightness;
        this.state = state;
    }
}
