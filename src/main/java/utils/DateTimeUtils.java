package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {
    private DateTimeUtils() {}

    public static LocalDateTime futureAtDaysHour(int plusDays, int hour, int minute) {
        return LocalDateTime.now()
                .plusDays(plusDays)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);
    }

    public static String[] futureTimeCandidates(LocalDateTime base) {
        // Returns 3 future 30-min spaced times in 24h format, from the next 30-min boundary after base+5m
        LocalDateTime t0 = base.plusMinutes(5);
        int minute = t0.getMinute();
        int toNext30 = (minute < 30) ? (30 - minute) : (60 - minute);
        LocalDateTime s1 = t0.plusMinutes(toNext30);
        LocalDateTime s2 = s1.plusMinutes(30);
        LocalDateTime s3 = s2.plusMinutes(30);
        DateTimeFormatter f24 = DateTimeFormatter.ofPattern("HH:mm");
        return new String[]{s1.format(f24), s2.format(f24), s3.format(f24)};
    }
}
