package com.tablenow.tablenow.domain.category.exception;

import com.tablenow.tablenow.global.exception.ErrorCode;
import com.tablenow.tablenow.global.exception.TablenowException;

import java.util.Map;

public class CategoryException extends TablenowException
{
    public CategoryException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
