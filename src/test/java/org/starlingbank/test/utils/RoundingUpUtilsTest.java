package org.starlingbank.test.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.starlingbank.test.utils.RoundingUpUtils.roundingUpWithBaseDenominator100;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class RoundingUpUtilsTest {

    @ParameterizedTest(name = "{index} => amount={0}, expectedRoundedUpAmount={1}")
    @MethodSource("valueExampleTable")
    void givenProperAmountWhenTestAmountToFulfilUpperRoundingThenRoundingUpAsExpected(final long amount,
                                                                                      final long expectedRoundedUpAmount) {

        assertEquals(expectedRoundedUpAmount, roundingUpWithBaseDenominator100(amount));
    }

    private static Stream<Arguments> valueExampleTable() {
        return Stream.of(
                Arguments.of(-12012, 0),
                Arguments.of(-5, 0),
                Arguments.of(0, 0),
                Arguments.of(2, 98),
                Arguments.of(2000, 0),
                Arguments.of(1001, 99)
        );
    }
}