package com.tablenow.tablenow.domain.auth.exception;

import com.tablenow.tablenow.global.exception.ErrorCode;

public class InvalidCredentialsException extends AuthException
{
    public InvalidCredentialsException() {
        super(ErrorCode.AUTH_INVALID_CREDENTIALS, null);
    }
}
