package org.starlingbank.test.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@UtilityClass
public class TimeUtils {

    /**
     * To convert provided Local date time to a string in a date format accepted by Starling bank API's
     * <p>
     * Can throw NullPointerException in case of null local date passed.
     *
     * @param localDate LocalDateTime format
     * @return String
     */
    public static String convertToStarlingTimestamp(final LocalDateTime localDate) {
        return formatDateTime(localDate);
    }

    private static String formatDateTime(final LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH));
    }
}
