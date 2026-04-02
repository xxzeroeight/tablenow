package com.tablenow.tablenow.domain.user.exception;

import com.tablenow.tablenow.global.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends UserException
{
    public UserNotFoundException(UUID userId) {
        super(ErrorCode.USER_NOT_FOUND, Map.of("user", userId));
    }

    public UserNotFoundException(String email) {
        super(ErrorCode.USER_NOT_FOUND, Map.of("email", email));
    }
}
