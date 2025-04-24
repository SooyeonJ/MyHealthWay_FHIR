package kr.co.iteyes.fhirmeta.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static final DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(DATEFORMATTER);
    }
}