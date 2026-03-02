package com.armydev.selfdev.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "입력값이 올바르지 않습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "INVALID_DATE_RANGE", "날짜 범위가 올바르지 않습니다."),
    INVALID_WEEK_START(HttpStatus.BAD_REQUEST, "INVALID_WEEK_START", "week_start는 월요일이어야 합니다."),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "액세스 토큰이 만료되었습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_INVALID", "유효하지 않은 리프레시 토큰입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "이메일 또는 비밀번호가 올바르지 않습니다."),

    // 403
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),

    // 404
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "플랜을 찾을 수 없습니다."),
    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "목표를 찾을 수 없습니다."),
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "태스크를 찾을 수 없습니다."),
    ROADMAP_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "로드맵을 찾을 수 없습니다."),
    USER_ROADMAP_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "사용자 로드맵을 찾을 수 없습니다."),
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "기록을 찾을 수 없습니다."),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "태그를 찾을 수 없습니다."),
    STAR_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "STAR 기록을 찾을 수 없습니다."),

    // 409
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", "이미 사용 중인 이메일입니다."),
    PLAN_ALREADY_EXISTS(HttpStatus.CONFLICT, "PLAN_ALREADY_EXISTS", "해당 주에 이미 플랜이 존재합니다."),
    STAR_ALREADY_EXISTS(HttpStatus.CONFLICT, "STAR_ALREADY_EXISTS", "해당 기록에 이미 STAR가 존재합니다."),
    TAG_ALREADY_EXISTS(HttpStatus.CONFLICT, "TAG_ALREADY_EXISTS", "이미 존재하는 태그입니다."),

    // 500
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
