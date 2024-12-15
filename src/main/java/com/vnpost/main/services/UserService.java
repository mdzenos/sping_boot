package com.vnpost.main.services;

import com.vnpost.main.models.User;
import com.vnpost.main.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Lấy tất cả người dùng
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Lấy người dùng theo ID, nếu không tìm thấy sẽ ném ra ngoại lệ
    public User getUserById(UUID id) {
        return userRepository.findOrFail(id, "User not found");
    }

    // Kiểm tra người dùng có tồn tại hay không
    public boolean checkUserExists(UUID id) {
        return userRepository.exists(id);
    }

    // Tạo người dùng mới
    public User createUser(User user) {
        // Bạn có thể thêm logic kiểm tra hoặc xử lý trước khi lưu người dùng mới
        return userRepository.save(user);
    }
}
