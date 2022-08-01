package org.starlingbank.test.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.starlingbank.test.utils.TimeUtils.convertToStarlingTimestamp;

class TimeUtilsTest {

    @Test
    void givenLocalDateTimeWhenConvertToStarlingTimestampThenTimeStampStringInStarlingFormat() {

        LocalDateTime localDateTime = LocalDateTime.of(2022, 8, 1, 12, 15);
        assertEquals("2022-08-01T12:15:00.000Z", convertToStarlingTimestamp(localDateTime));
    }

    @Test
    void givenNullLocalDateTimeWhenConvertToStarlingTimestampThenNullPointerException() {

        assertThrowsExactly(NullPointerException.class, () -> convertToStarlingTimestamp(null));
    }
}