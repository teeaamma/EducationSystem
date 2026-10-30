package com.course.krch.educationsystem.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, DateRange> {
    @Override
    public boolean isValid(DateRange dateRange, ConstraintValidatorContext constraintValidatorContext) {
        if (dateRange == null || dateRange.startDate() == null || dateRange.endDate() == null)
            return true;

        return dateRange.startDate().isBefore(dateRange.endDate());
    }
}
