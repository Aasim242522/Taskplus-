package com.taskmonitor.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INTERNAL_SERVER_ERROR("ERR_001", "An internal error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    RESOURCE_NOT_FOUND("ERR_002", "Resource not found", HttpStatus.NOT_FOUND),
    VALIDATION_FAILED("ERR_003", "Validation failed", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("ERR_004", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("ERR_005", "Access is forbidden", HttpStatus.FORBIDDEN),
    USER_ALREADY_EXISTS("ERR_006", "User with this email already exists", HttpStatus.CONFLICT),
    TASK_ALREADY_COMPLETED("ERR_007", "Task is already marked as completed", HttpStatus.CONFLICT),
    OPTIMISTIC_LOCK_CONFLICT("ERR_008", "Task was modified by another transaction", HttpStatus.CONFLICT),
    RATE_LIMIT_EXCEEDED("ERR_009", "Too many requests. Please try again later", HttpStatus.TOO_MANY_REQUESTS),
    INVALID_STATUS_TRANSITION("ERR_010", "Invalid status transition", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    public String getCode() { return code; }
    public String getDefaultMessage() { return defaultMessage; }
    public HttpStatus getHttpStatus() { return httpStatus; }
}
