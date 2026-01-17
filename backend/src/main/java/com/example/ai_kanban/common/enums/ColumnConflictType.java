package com.example.ai_kanban.common.enums;

import com.example.ai_kanban.common.utils.ExceptionUtils;
import lombok.Getter;
import org.springframework.dao.DuplicateKeyException;

import java.util.function.Predicate;

@Getter
public enum ColumnConflictType {

    NAME(ExceptionUtils::isColumnNameConflict),
    ORDER(ExceptionUtils::isColumnOrderConflict);

    private final Predicate<DuplicateKeyException> matcher;

    ColumnConflictType(Predicate<DuplicateKeyException> matcher) {
        this.matcher = matcher;
    }

    public boolean matches(DuplicateKeyException e) {
        return matcher.test(e);
    }
}
