package com.vnpost.main.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GlobalRateLimitService {

    private final ConcurrentHashMap<String, Long> requestCounts = new ConcurrentHashMap<>();

    @Value("${rate.limit.global.limit:999999999}")
    private long rateLimit; // Giới hạn yêu cầu với giá trị mặc định

    @Value("${rate.limit.global.window:30000}")
    private long timeWindow; // Cửa sổ thời gian với giá trị mặc định

    public boolean isRateLimited() {
        long currentTime = System.currentTimeMillis();

        // Tính số lượng yêu cầu trong thời gian hợp lệ
        long requestCount = requestCounts.values().stream()
                .filter(timestamp -> timestamp >= currentTime - timeWindow)
                .count();

        if (requestCount >= rateLimit) {
            return true; // Quá giới hạn
        }

        // Lưu lại yêu cầu mới
        requestCounts.put(String.valueOf(currentTime), currentTime);
        return false;
    }

    // Job dọn dẹp định kỳ
    @Scheduled(fixedRate = 30000) // Chạy mỗi 30 giây
    public void cleanupOldRequests() {
        long currentTime = System.currentTimeMillis();
        Iterator<Long> iterator = requestCounts.values().iterator();
        while (iterator.hasNext()) {
            if (currentTime - iterator.next() > timeWindow) {
                iterator.remove(); // Xóa yêu cầu cũ
            }
        }
    }
}
