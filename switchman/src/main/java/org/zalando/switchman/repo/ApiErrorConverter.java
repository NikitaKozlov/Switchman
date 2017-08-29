package org.zalando.switchman.repo;

import org.zalando.switchman.api.ApiResponse;

public interface ApiErrorConverter {
    Response.Cause convertApiError(ApiResponse.ApiError apiError);
}
