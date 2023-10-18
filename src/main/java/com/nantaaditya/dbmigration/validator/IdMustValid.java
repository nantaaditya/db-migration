package com.nantaaditya.dbmigration.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IdMustValidValidator.class)
@Documented
public @interface IdMustValid {
  String message() default "must valid";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };
}
