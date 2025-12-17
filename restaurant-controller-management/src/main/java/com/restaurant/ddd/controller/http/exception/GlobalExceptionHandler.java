package com.restaurant.ddd.controller.http.exception;

import com.restaurant.ddd.controller.http.model.enums.ResultUtil;
import com.restaurant.ddd.controller.http.model.vo.ResultMessage;
import com.restaurant.ddd.domain.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * Global Exception Handler
 * Handles all exceptions and returns consistent error messages to client
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors (e.g., @Valid, @NotNull, etc.)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultMessage<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.error("Validation error: {}", errorMessage);
        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.ERROR.code(), "Dữ liệu không hợp lệ: " + errorMessage));
    }

    /**
     * Handle type mismatch errors (e.g., wrong UUID format)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResultMessage<Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("Tham số '%s' không đúng định dạng", ex.getName());
        log.error("Type mismatch error: {}", errorMessage, ex);
        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.ERROR.code(), errorMessage));
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResultMessage<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.ERROR.code(), ex.getMessage()));
    }

    /**
     * Handle illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResultMessage<Object>> handleIllegalStateException(IllegalStateException ex) {
        log.error("Illegal state: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(ResultUtil.error(ResultCode.ERROR.code(), ex.getMessage()));
    }

    /**
     * Handle null pointer exceptions
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResultMessage<Object>> handleNullPointerException(NullPointerException ex) {
        log.error("Null pointer exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultUtil.error(ResultCode.ERROR.code(), "Lỗi hệ thống: Dữ liệu không tồn tại"));
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultMessage<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        String message = ex.getMessage() != null ? ex.getMessage() : "Đã xảy ra lỗi không xác định";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultUtil.error(ResultCode.ERROR.code(), "Lỗi hệ thống: " + message));
    }
}
