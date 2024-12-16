package com.vnpost.api.v1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.vnpost.api.v1.filters.RateLimitFilter;
import com.vnpost.api.v1.middleware.MiddlewareInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private FileSystem fileStorageProperties;

    @Autowired
    private MiddlewareInterceptor middlewareInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + fileStorageProperties.getFilesDir() + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Thêm MiddlewareInterceptor với các đường dẫn đã cấu hình
        registry.addInterceptor(middlewareInterceptor)
                .addPathPatterns(middlewareInterceptor.URL_PATTERNS)
                .excludePathPatterns(middlewareInterceptor.EXCLUDED_URLS);
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilter(
            @Value("${rate.limit.max:999999999}") int capacity,
            @Value("${rate.limit.time:60}") int refill) {
        FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitFilter(capacity, refill));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
