package com.taskmonitor.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private Instant timestamp = Instant.now();
    private String traceId = UUID.randomUUID().toString();
    private List<ValidationError> errors;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, T data, String message, Instant timestamp, String traceId, List<ValidationError> errors) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
        this.traceId = traceId != null ? traceId : UUID.randomUUID().toString();
        this.errors = errors;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, "Operation successful", Instant.now(), UUID.randomUUID().toString(), null);
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, message, Instant.now(), UUID.randomUUID().toString(), null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message, Instant.now(), UUID.randomUUID().toString(), null);
    }

    public static <T> ApiResponse<T> error(String message, List<ValidationError> errors) {
        return new ApiResponse<>(false, null, message, Instant.now(), UUID.randomUUID().toString(), errors);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    public static class ValidationError {
        private String field;
        private String message;

        public ValidationError() {
        }

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
