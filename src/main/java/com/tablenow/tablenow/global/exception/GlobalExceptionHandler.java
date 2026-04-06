package com.tablenow.tablenow.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    /* TableNowException(4xx) */
    @ExceptionHandler(TablenowException.class)
    public ResponseEntity<ErrorResponse> handleTableNowException(TablenowException ex) {
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(ErrorResponse.from(ex));
    }

    /* AllException(500) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        Instant.now(),
                        "INTERNAL_SERVER_ERROR",
                        "예상치 못한 오류가 발생했습니다.",
                        Map.of(),
                        ex.getClass().getSimpleName(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }

    /* 메서드 레벨 권한 거부 */
    /* AuthorizationDeniedException(@PreAuthorize, 403) */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        Instant.now(),
                        "FORBIDDEN",
                        "접근 권한이 없습니다.",
                        Map.of(),
                        ex.getClass().getSimpleName(),
                        HttpStatus.FORBIDDEN.value()
                ));
    }

    /* Validation(400) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        Map<String, Object> details = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "요청 데이터가 유효하지 않습니다."
                ));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        Instant.now(),
                        "BAD_REQUEST",
                        "요청 데이터가 유효하지 않습니다.",
                        details,
                        ex.getClass().getSimpleName(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }
}
