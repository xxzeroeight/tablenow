package com.tablenow.tablenow.domain.restaurant.exception;

import com.tablenow.tablenow.global.exception.ErrorCode;
import com.tablenow.tablenow.global.exception.TablenowException;

import java.util.Map;

public class RestaurantException extends TablenowException
{
    public RestaurantException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
