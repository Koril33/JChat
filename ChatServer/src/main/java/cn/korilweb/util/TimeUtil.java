package cn.korilweb.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private final static String ZONE_ID = "UTC+8";

    private final static String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";


    /**
     * 当前的时间戳通过指定的模式转换成字符串
     * @return 当前的时间戳的字符串表示
     */
    public static String nowStr() {
        return instantToStr(Instant.now());
    }


    /**
     * 时间戳转成字符串
     * @param instant 时间戳
     * @return 时间戳的字符串表示
     */
    public static String instantToStr(Instant instant) {
        return instant.atZone(ZoneId.of(ZONE_ID)).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN));
    }
}
