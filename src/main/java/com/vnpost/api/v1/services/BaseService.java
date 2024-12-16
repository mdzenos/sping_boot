package com.vnpost.api.v1.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnpost.api.v1.config.FileSystem;
import com.vnpost.api.v1.exceptions.JsonException;

@Data
@Service
public class BaseService {

    @Autowired
    private FileSystem fileStorageProperties;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    private List<String> fileSystemAllowed = List.of("png", "xlsx");

    public String uploadFile(MultipartFile file, String folder, List<String> arrAllowed) throws IOException {

        // Sử dụng thư mục mặc định nếu folder là null hoặc rỗng
        folder = (folder == null || folder.isEmpty()) ? "uploads" : folder;

        // Kiểm tra kích thước tệp tải lên
        if (file.getSize() > parseSize(maxFileSize)) {
            return "Tệp vượt quá kích thước tối đa(" + maxFileSize + ")";
        }

        // Kiểm tra định dạng tệp
        String fileExtension = getFileExtension(file);
        String validationMessage = validateFileExtension(fileExtension, arrAllowed);
        if (validationMessage != null) {
            return validationMessage;
        }

        // Tạo thư mục nếu chưa tồn tại
        String uploadDir = "/storage/files/" + folder;
        Path dirPath = Paths.get(uploadDir);
        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Tạo tên tệp mới và lưu
        String newFileName = System.currentTimeMillis() + generateRandomString(6) + "." + fileExtension;
        Path dest = dirPath.resolve(newFileName);
        file.transferTo(dest.toFile());

        return "Tệp đã được tải lên: " + dest.toAbsolutePath();
    }

    private String validateFileExtension(String fileExtension, List<String> arrAllowed) {
        if (!fileSystemAllowed.contains(fileExtension)) {
            return "Hệ thống không chấp nhận định dạng";
        }
        if (!arrAllowed.contains(fileExtension)) {
            return "Tệp không hợp lệ, chỉ cho phép các định dạng: " + String.join(", ", arrAllowed);
        }
        return null;
    }

    public String generateRandomString(int length, boolean... number) {
        boolean isNumber = (number.length > 0) && number[0];
        String characters = isNumber ? "0123456789" : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder randomString = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }

        return randomString.toString();
    }

    public String getFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }

    public long parseSize(String size) {
        String sizeUpper = size.toUpperCase();
        if (sizeUpper.endsWith("KB")) {
            return Long.parseLong(sizeUpper.replace("KB", "")) * 1024;
        } else if (sizeUpper.endsWith("MB")) {
            return Long.parseLong(sizeUpper.replace("MB", "")) * 1024 * 1024;
        } else if (sizeUpper.endsWith("GB")) {
            return Long.parseLong(sizeUpper.replace("GB", "")) * 1024 * 1024 * 1024;
        }
        return Long.parseLong(size);
    }

    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<String, Consumer<String>> logLevels = Map.of(
            "trace", logger::trace,
            "debug", logger::debug,
            "info", logger::info,
            "warn", logger::warn,
            "error", logger::error);

    public static <T> void log(String level, String message, T loggerData) {
        String logMessage = message + ": " + convertToString(loggerData);
        logLevels.getOrDefault(level.toLowerCase(), logger::info).accept(logMessage);
    }

    private static <T> String convertToString(T loggerData) {
        if (loggerData == null) {
            return "null";
        }
        try {
            return objectMapper.writeValueAsString(loggerData);
        } catch (Exception e) {
            throw new JsonException("Error converting to String");
        }
    }

}
