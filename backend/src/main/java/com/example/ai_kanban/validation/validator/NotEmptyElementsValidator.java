package com.example.ai_kanban.validation.validator;

import com.example.ai_kanban.validation.annotation.NotEmptyElements;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Objects;

public class NotEmptyElementsValidator implements ConstraintValidator<NotEmptyElements, Object> {

    private boolean allowNullCollection;

    @Override
    public void initialize(NotEmptyElements constraintAnnotation) {
        this.allowNullCollection=constraintAnnotation.allowNullCollection();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {

        if(value==null){
            return allowNullCollection;
        }

        if(value instanceof Collection<?> collection){
            if(collection.isEmpty()){
                return false;
            }
            return collection.stream().noneMatch(Objects::isNull);
        }

        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            if (length == 0) return false;

            for (int i = 0; i < length; i++) {
                if (Array.get(value, i) == null) {
                    return false;
                }
            }
            return true;
        }


        return true;
    }
}
