package com.tnt.aggregator.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = PricingListConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface PricingListConstraint {
    String message() default "One of the items is not a valid ISO 2 country code.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}