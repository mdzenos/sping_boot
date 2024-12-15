package com.vnpost.main.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.vnpost.main.utils.StatusCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseService<T> {

        private boolean success;
        private T data;
        private String message;
        private long totalRecord;
        private List<String> actions;
        private int statusCode;

        public static <T> ResponseEntity<ResponseService<T>> sendSuccess(T data, String message, long totalRecord,
                        Integer statusCode, List<String> actions) {
                return buildResponse(true,
                                message,
                                data,
                                totalRecord,
                                Optional.ofNullable(actions).orElse(Collections.emptyList()),
                                Optional.ofNullable(statusCode).orElse(StatusCode.OK));
        }

        public static <T> ResponseEntity<ResponseService<T>> sendError(String errorMessage,
                        List<String> errorData, Integer statusCode) {

                return buildResponse(
                                false,
                                errorMessage,
                                (T) Optional.ofNullable(errorData).orElse(Collections.emptyList()),
                                0,
                                Collections.emptyList(),
                                Optional.ofNullable(statusCode).orElse(StatusCode.BAD_REQUEST));
        }

        private static <T> ResponseEntity<ResponseService<T>> buildResponse(boolean success, String message, T data,
                        long totalRecord, List<String> actions, int statusCode) {

                ResponseService<T> response = ResponseService.<T>builder()
                                .success(success)
                                .message(message)
                                .data(data)
                                .totalRecord(totalRecord)
                                .actions(actions)
                                .build();
                return ResponseEntity.status(statusCode).body(response);
        }
}
