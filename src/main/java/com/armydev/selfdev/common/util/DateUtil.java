package com.armydev.selfdev.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

public final class DateUtil {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private DateUtil() {}

    public static LocalDate getMondayOfCurrentWeek() {
        return LocalDate.now(SEOUL)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public static LocalDate getSundayOfWeek(LocalDate monday) {
        return monday.plusDays(6);
    }

    public static boolean isMonday(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.MONDAY;
    }

    public static long dDayUntil(LocalDate target) {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(SEOUL), target);
    }
}
