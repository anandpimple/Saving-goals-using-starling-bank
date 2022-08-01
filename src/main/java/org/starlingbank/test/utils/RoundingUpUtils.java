package org.starlingbank.test.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RoundingUpUtils {

    /**
     * Method calculates the number to round up the given positive value to the base denominator 100.
     * (Note: Will return 0 if negative value is passed)
     * <p>
     * Please find some examples for more clarity;
     * <p>
     * ---------------------------------
     * | Value        | Expected Outcome |
     * ----------------------------------
     * | 1009         | 91               |
     * | 0            | 0                |
     * | 2            | 98               |
     * | -1201        | 0                |
     * | 2400         | 0                |
     * -----------------------------------
     *
     * @param value Value for rounding up
     * @return long
     */
    public static long roundingUpWithBaseDenominator100(final long value) {

        return roundingUpFor(value, 100);
    }

    private static long roundingUpFor(final long value,
                                      final long baseDenominator) {

        long reminder = value % baseDenominator;
        return reminder > 0 ? baseDenominator - reminder : 0;
    }
}
