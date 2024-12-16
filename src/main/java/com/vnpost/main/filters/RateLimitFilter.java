package com.vnpost.main.filters;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnpost.main.services.ResponseService;
import com.vnpost.main.utils.StatusCode;

public class RateLimitFilter implements Filter {

    private final Bucket bucket;

    public RateLimitFilter(
            @Value("${rate.limit.max:999999999}") int capacity,
            @Value("${rate.limit.time:60}") int refill) {
        this.bucket = Bucket.builder()
                .addLimit(limit -> limit.capacity(capacity).refillIntervally(capacity, Duration.ofSeconds(refill)))
                .build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            ResponseEntity<ResponseService<Object>> responseEntity = ResponseService.<Object>sendError(
                    "Rate limit exceeded. Please try again later.",
                    Collections.emptyList(),
                    StatusCode.TOO_MANY_REQUESTS);
            ResponseService<Object> responseService = responseEntity.getBody();
            httpResponse.setStatus(StatusCode.TOO_MANY_REQUESTS);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(new ObjectMapper().writeValueAsString(responseService));
            return;
        }
    }
}
