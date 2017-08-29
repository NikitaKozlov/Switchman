package org.zalando.switchman.api;

public class ApiResponse {

    public static ApiResponse createSuccessfulResponse() {
        return new ApiResponse(HttpStatus.OK.value(), true, null);
    }

    public static ApiResponse createFailedResponse(final int code, final ApiError cause) {
        return new ApiResponse(code, false, cause);
    }

    private final int code;
    private final boolean isSuccessful;
    private final ApiError cause;

    private ApiResponse(final int code, final boolean isSuccessful, final ApiError cause) {
        this.code = code;
        this.isSuccessful = isSuccessful;
        this.cause = cause;
    }

    public int getCode() {
        return code;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public ApiError getCause() {
        return cause;
    }

    public interface ApiError { }
}
