package com.armydev.selfdev.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final T data;
    private final ErrorBody error;

    private ApiResponse(T data, ErrorBody error) {
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> error(ErrorBody error) {
        return new ApiResponse<>(null, error);
    }

    @Getter
    public static class ErrorBody {
        private final String code;
        private final String message;
        private final int status;
        private final String timestamp;
        private final String path;
        private final Object details;

        public ErrorBody(String code, String message, int status, String timestamp, String path, Object details) {
            this.code = code;
            this.message = message;
            this.status = status;
            this.timestamp = timestamp;
            this.path = path;
            this.details = details;
        }
    }
}
