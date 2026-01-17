package com.example.ai_kanban.validation.annotation;

import com.example.ai_kanban.validation.validator.NotEmptyElementsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyElementsValidator.class)
public @interface NotEmptyElements {
    String message() default "集合不能为空，且集合中元素不能为null";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNullCollection() default false;
}
