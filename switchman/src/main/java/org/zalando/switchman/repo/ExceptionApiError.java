package org.zalando.switchman.repo;

import org.zalando.switchman.api.ApiResponse;

class ExceptionApiError implements ApiResponse.ApiError {
    private final Throwable throwable;

    public ExceptionApiError(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getCause() {
        return throwable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExceptionApiError that = (ExceptionApiError) o;

        return throwable != null ? throwable.equals(that.throwable) : that.throwable == null;

    }

    @Override
    public int hashCode() {
        return throwable != null ? throwable.hashCode() : 0;
    }
}
