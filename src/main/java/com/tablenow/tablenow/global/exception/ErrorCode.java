package com.tablenow.tablenow.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode
{
    // users
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // auth
    AUTH_INVALID_CREDENTIALS("이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    AUTH_INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED),
    AUTH_DUPLICATE_EMAIL("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),

    // category
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // restaurant
    RESTAURANT_NOT_FOUND("레스트랑을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RESTAURANT_ACCESS_DENIED("해당 레스토랑에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final int status;
    private final String message;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status.value();
    }
}
