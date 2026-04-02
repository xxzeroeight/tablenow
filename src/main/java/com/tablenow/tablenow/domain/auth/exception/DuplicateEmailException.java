package com.tablenow.tablenow.domain.auth.exception;

import com.tablenow.tablenow.global.exception.ErrorCode;

import java.util.Map;

public class DuplicateEmailException extends AuthException
{
    public DuplicateEmailException(String email) {
        super(ErrorCode.AUTH_DUPLICATE_EMAIL, Map.of("email", email));
    }
}
