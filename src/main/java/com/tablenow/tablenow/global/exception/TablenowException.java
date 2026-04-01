package com.tablenow.tablenow.global.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class TablenowException extends RuntimeException
{
    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details; // 예외 발생 상황에 대한 추가정보

    public TablenowException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details != null ? details : Map.of();
    }
}
