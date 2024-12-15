package com.vnpost.main.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.vnpost.main.interceptors.RateLimitInterceptor;
import com.vnpost.main.middleware.MiddlewareInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private FileSystem fileStorageProperties;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Autowired
    private MiddlewareInterceptor middlewareInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + fileStorageProperties.getFilesDir() + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Thêm RateLimitInterceptor
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/**");

        // Thêm MiddlewareInterceptor với các đường dẫn đã cấu hình
        registry.addInterceptor(middlewareInterceptor)
                .addPathPatterns(middlewareInterceptor.URL_PATTERNS)
                .excludePathPatterns(middlewareInterceptor.EXCLUDED_URLS);
    }
}
