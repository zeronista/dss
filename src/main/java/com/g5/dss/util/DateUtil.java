package com.g5.dss.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateUtil {

    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(FORMATTER) : null;
    }

    public static LocalDateTime parse(String dateTimeStr) {
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr, FORMATTER) : null;
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static int daysBetween(LocalDateTime start, LocalDateTime end) {
        return (int) java.time.Duration.between(start, end).toDays();
    }
}

