package br.com.fiap.postech.restaurantsync.infrastructure.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;

public class ValidTimeIntervalValidator implements ConstraintValidator<ValidTimeInterval, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        try {
            String[] times = value.split("-");
            LocalTime start = LocalTime.parse(times[0]);
            LocalTime end = LocalTime.parse(times[1]);
            return end.isAfter(start);
        } catch (Exception e) {
            return false;
        }
    }
}
