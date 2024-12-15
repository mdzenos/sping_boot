package com.vnpost.main.middleware;

import org.springframework.stereotype.Service;

@Service
public class MiddlewareList {

    public String checkLogin() {
        // Logic kiểm tra người dùng đã đăng nhập chưa
        boolean isLoggedIn = false; // Kiểm tra đăng nhập thực tế từ request
        if (!isLoggedIn) {
            return "User is not logged in.";
        }
        return null; // Không có lỗi
    }
}
