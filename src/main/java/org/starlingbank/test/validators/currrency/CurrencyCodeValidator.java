package org.starlingbank.test.validators.currrency;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

/**
 * To check if the 3 letters String passed is expected as per the available currency code.
 */
public class CurrencyCodeValidator implements ConstraintValidator<CurrencyCode, String> {
    private final static List<String> codes = Currency.getAvailableCurrencies().stream()
            .map(Currency::getCurrencyCode).sorted().toList();

    //Context for ConstraintValidator can get modified, hence not final.
    @Override
    public boolean isValid(final String value,
                           ConstraintValidatorContext context) {

        return StringUtils.hasText(value) && codes.contains(value);
    }

}
