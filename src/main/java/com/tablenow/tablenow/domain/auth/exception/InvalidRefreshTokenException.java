package com.tablenow.tablenow.domain.auth.exception;

import com.tablenow.tablenow.global.exception.ErrorCode;

public class InvalidRefreshTokenException extends AuthException
{
    public InvalidRefreshTokenException() {
        super(ErrorCode.AUTH_INVALID_REFRESH_TOKEN, null);
    }
}
