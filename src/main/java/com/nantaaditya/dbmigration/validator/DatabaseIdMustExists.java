package com.nantaaditya.dbmigration.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DatabaseIdMustExistsValidator.class)
@Documented
public @interface DatabaseIdMustExists {
  String message() default "NotExists";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };
}
