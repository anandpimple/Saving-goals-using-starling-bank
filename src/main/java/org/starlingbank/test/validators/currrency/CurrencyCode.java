package org.starlingbank.test.validators.currrency;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyCodeValidator.class)
@Documented
public @interface CurrencyCode {
    String message() default "Invalid code. Expected ISO-4271 3 Letters currency code";
}
