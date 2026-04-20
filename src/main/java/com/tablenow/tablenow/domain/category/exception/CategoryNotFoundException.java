package com.tablenow.tablenow.domain.category.exception;

import com.tablenow.tablenow.global.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class CategoryNotFoundException extends CategoryException
{
    public CategoryNotFoundException(UUID categoryId) {
        super(ErrorCode.CATEGORY_NOT_FOUND, Map.of("categoryId", categoryId));
    }
}
