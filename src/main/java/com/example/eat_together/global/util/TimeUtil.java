package com.example.eat_together.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatNow() {
        return LocalDateTime.now().format(formatter);
    }

    public static String format(LocalDateTime time) {
        return time.format(formatter);
    }
}
