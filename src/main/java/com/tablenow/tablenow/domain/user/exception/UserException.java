package com.tablenow.tablenow.domain.user.exception;

import com.tablenow.tablenow.global.exception.ErrorCode;
import com.tablenow.tablenow.global.exception.TablenowException;

import java.util.Map;

public class UserException extends TablenowException
{
    public UserException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
