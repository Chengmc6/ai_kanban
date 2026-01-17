package com.example.ai_kanban.common.interfaces;

import org.springframework.dao.DuplicateKeyException;

public interface ConflictDetector {
    boolean match(DuplicateKeyException e);
}
