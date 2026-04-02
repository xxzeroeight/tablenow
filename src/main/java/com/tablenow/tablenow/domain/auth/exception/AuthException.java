package com.tablenow.tablenow.domain.auth.exception;

import com.tablenow.tablenow.global.exception.ErrorCode;
import com.tablenow.tablenow.global.exception.TablenowException;

import java.util.Map;

public class AuthException extends TablenowException
{
    public  AuthException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
