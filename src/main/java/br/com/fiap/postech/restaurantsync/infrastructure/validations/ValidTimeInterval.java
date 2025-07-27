package br.com.fiap.postech.restaurantsync.infrastructure.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTimeIntervalValidator.class)
public @interface ValidTimeInterval {
    String message() default "Closing time must be after opening time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
