package org.starlingbank.test.validators.currrency;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrencyCodeValidatorTest {

    private final CurrencyCodeValidator underTest = new CurrencyCodeValidator();

    @ParameterizedTest(name = "{index} => regex={0}, isValid={1}")
    @MethodSource("currencyExampleTable")
    void givenCurrencyStringWhenValidateThenBooleanValueAsExpected(final String currency,
                                                                   final boolean expected) {

        assertEquals(expected, underTest.isValid(currency, null));
    }

    private static Stream<Arguments> currencyExampleTable() {
        return Stream.of(
                Arguments.of("GBP", true),
                Arguments.of("INR", true),
                Arguments.of("GB", false),
                Arguments.of("XYZ", false),
                Arguments.of(" ", false),
                Arguments.of(null, false)
        );
    }
}