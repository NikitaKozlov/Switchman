package org.zalando.switchman.api;

import org.zalando.switchman.repo.ApiErrorConverter;
import org.zalando.switchman.repo.Response;


public class ApiErrorConverterImpl implements ApiErrorConverter {
    @Override
    public Response.Cause convertApiError(ApiResponse.ApiError apiError) {
        return new Response.Cause() {
        };
    }
}
