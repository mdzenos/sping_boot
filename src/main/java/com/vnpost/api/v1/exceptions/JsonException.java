package com.vnpost.api.v1.exceptions;

public class JsonException extends RuntimeException {

    // Constructor chỉ với thông điệp lỗi
    public JsonException(String message) {
        super(message);
    }

}