package org.starlingbank.test.validators.uuid;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UuidValidatorTest {

    @Mock
    private Uuid mockUuid;

    @ParameterizedTest(name = "{index} => regex={1}, isValid={2}")
    @MethodSource("uuidExampleTable")
    void givenCurrencyStringWhenValidateThenBooleanValueAsExpected(final String regex,
                                                                   final String uuid,
                                                                   final boolean expected) {

        when(mockUuid.regex()).thenReturn(regex);

        UuidValidator underTest = new UuidValidator();
        underTest.initialize(mockUuid);

        assertEquals(expected, underTest.isValid(uuid, null));

        //Not needed as assert above covers this. Just to show the possibility of verification
        verify(mockUuid).regex();
    }

    private static Stream<Arguments> uuidExampleTable() {
        return Stream.of(
                Arguments.of("A", "bbebc062-5216-465c-bf34-81b3191135a4", false),
                Arguments.of("A", "A", true),
                Arguments.of("[aA-zZ0-9]{8}[-][aA-zZ0-9]{4}[-][aA-zZ0-9]{4}[-][aA-zZ0-9]{4}[-][aA-zZ0-9]{12}", "bbebc062-5216-465c-bf34-81b3191135a4", true),
                Arguments.of("[aA-zZ0-9]{8}[-][aA-zZ0-9]{4}[-][aA-zZ0-9]{4}[-][aA-zZ0-9]{4}[-][aA-zZ0-9]{12}", "A", false)
        );
    }
}