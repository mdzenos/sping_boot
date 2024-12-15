package com.vnpost.main.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnpost.main.services.GlobalRateLimitService;
import com.vnpost.main.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final String APPLICATION_JSON = "application/json";
    private static final Map<String, Object> RATE_LIMIT_RESPONSE = Map.of(
            "success", false,
            "message", "Đã vượt quá giới hạn truy cập! Vui lòng thử lại sau.",
            "data", Collections.emptyList(),
            "totalRecord", 0,
            "actions", Collections.emptyList());

    @Autowired
    private GlobalRateLimitService globalRateLimitService;

    @Autowired
    private ObjectMapper objectMapper; // Bean dùng chung, tránh khởi tạo mới.

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // Kiểm tra giới hạn truy cập
        if (globalRateLimitService.isRateLimited()) {
            // Tạo JSON response trả về
            String jsonResponse = objectMapper.writeValueAsString(RATE_LIMIT_RESPONSE);

            response.setStatus(StatusCode.TOO_MANY_REQUESTS);
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write(jsonResponse);
            return false; // Dừng xử lý nếu vượt quá giới hạn
        }
        return true; // Tiếp tục nếu hợp lệ
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
    }
}
