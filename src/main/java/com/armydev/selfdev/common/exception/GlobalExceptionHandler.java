package com.armydev.selfdev.common.exception;

import com.armydev.selfdev.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusiness(BusinessException e, HttpServletRequest request) {
        log.warn("BusinessException: {} - {}", e.getErrorCode().getCode(), e.getMessage());
        ErrorCode code = e.getErrorCode();
        return ResponseEntity
            .status(code.getStatus())
            .body(ApiResponse.error(new ApiResponse.ErrorBody(
                code.getCode(),
                e.getMessage(),
                code.getStatus().value(),
                LocalDateTime.now().format(FORMATTER),
                request.getRequestURI(),
                null
            )));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException e,
                                                            HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ErrorCode code = ErrorCode.VALIDATION_ERROR;
        return ResponseEntity
            .status(code.getStatus())
            .body(ApiResponse.error(new ApiResponse.ErrorBody(
                code.getCode(),
                code.getMessage(),
                code.getStatus().value(),
                LocalDateTime.now().format(FORMATTER),
                request.getRequestURI(),
                fieldErrors
            )));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleNotReadable(HttpMessageNotReadableException e,
                                                             HttpServletRequest request) {
        ErrorCode code = ErrorCode.VALIDATION_ERROR;
        return ResponseEntity
            .status(code.getStatus())
            .body(ApiResponse.error(new ApiResponse.ErrorBody(
                code.getCode(),
                "요청 본문을 읽을 수 없습니다.",
                code.getStatus().value(),
                LocalDateTime.now().format(FORMATTER),
                request.getRequestURI(),
                null
            )));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        ErrorCode code = ErrorCode.FORBIDDEN;
        return ResponseEntity
            .status(code.getStatus())
            .body(ApiResponse.error(new ApiResponse.ErrorBody(
                code.getCode(),
                code.getMessage(),
                code.getStatus().value(),
                LocalDateTime.now().format(FORMATTER),
                request.getRequestURI(),
                null
            )));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleUnexpected(Exception e, HttpServletRequest request) {
        log.error("Unexpected error", e);
        ErrorCode code = ErrorCode.INTERNAL_ERROR;
        return ResponseEntity
            .status(code.getStatus())
            .body(ApiResponse.error(new ApiResponse.ErrorBody(
                code.getCode(),
                code.getMessage(),
                code.getStatus().value(),
                LocalDateTime.now().format(FORMATTER),
                request.getRequestURI(),
                null
            )));
    }
}
