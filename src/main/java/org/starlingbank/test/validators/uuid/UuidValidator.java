package org.starlingbank.test.validators.uuid;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class UuidValidator implements ConstraintValidator<Uuid, String> {

    private String regex;

    public void initialize(Uuid uuid) {
        this.regex = uuid.regex();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        return StringUtils.hasText(value) && Pattern.matches(regex, value);
    }

}
