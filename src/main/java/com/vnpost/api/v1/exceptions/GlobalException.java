package com.vnpost.api.v1.exceptions;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.vnpost.api.v1.services.ResponseService;
import com.vnpost.api.v1.utils.StatusCode;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
@Order(1)
public class GlobalException {
    @ExceptionHandler({ Exception.class, IllegalArgumentException.class, NullPointerException.class,
            RuntimeException.class, ResourceNotFoundException.class, MiddlewareException.class })
    public ResponseEntity<ResponseService<String[]>> handle(final Exception exception, final WebRequest request) {
        String errorMessage;
        int statusCode;

        // Xử lý các loại exception cụ thể và gán thông báo lỗi + mã trạng thái
        if (exception instanceof MiddlewareException) {
            errorMessage = exception.getMessage();
            statusCode = StatusCode.UNAUTHORIZED;
        } else if (exception instanceof ResourceNotFoundException) {
            errorMessage = "Resource not found: " + exception.getMessage();
            statusCode = StatusCode.NOT_FOUND;
        } else if (exception instanceof IllegalArgumentException) {
            errorMessage = "Invalid argument: " + exception.getMessage();
            statusCode = StatusCode.BAD_REQUEST;
        } else if (exception instanceof NullPointerException) {
            errorMessage = "A null value was encountered where it was not expected.";
            statusCode = StatusCode.INTERNAL_SERVER_ERROR;
        } else if (exception instanceof RuntimeException) {
            errorMessage = "Có lỗi khi thực thi: " + exception.getMessage();
            statusCode = StatusCode.INTERNAL_SERVER_ERROR;
        } else {
            errorMessage = "An unexpected error occurred: " + exception.getMessage();
            statusCode = StatusCode.INTERNAL_SERVER_ERROR;
        }

        List<String> errorData = Collections.singletonList(errorMessage);
        return ResponseService.sendError(errorMessage, errorData, statusCode);
    }
}
