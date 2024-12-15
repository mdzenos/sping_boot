package com.vnpost.main.controllers;

import com.vnpost.main.services.UserService;
import com.vnpost.main.services.ResponseService;
import com.vnpost.main.middleware.Middleware;
import com.vnpost.main.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Lấy danh sách tất cả người dùng
    @GetMapping()
    public ResponseEntity<ResponseService<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseService.sendSuccess(users, "Danh sách người dùng", users.size(), 200, null);
    }

    // Tạo người dùng mới
    @PostMapping
    public ResponseEntity<ResponseService<User>> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseService.sendSuccess(createdUser, "Tạo người dùng thành công", 1, 201, null);
    }
}
