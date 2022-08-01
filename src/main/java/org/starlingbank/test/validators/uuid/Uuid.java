package org.starlingbank.test.validators.uuid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UuidValidator.class)
@Documented
public @interface Uuid {
    String message() default "Invalid uuid";

    String regex() default "[aA-zZ0-9]{8}[-][aA-zZ0-9]{4}[-][aA-zZ0-9]{4}[-][aA-zZ0-9]{4}[-][aA-zZ0-9]{12}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
