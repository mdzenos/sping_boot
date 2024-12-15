package com.vnpost.main.middleware;

import com.vnpost.main.exceptions.MiddlewareException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import java.lang.reflect.Method;

@Component
public class MiddlewareInterceptor implements HandlerInterceptor {

    // URL nằm trong nhóm ngoại lệ
    public static final String[] EXCLUDED_URLS = { "/api/admin/public", "/api/admin/login" };

    // URL nằm trong nhóm cần kiểm tra
    public static final String[] URL_PATTERNS = { "/api/**" };

    // Danh sách method luôn kiểm tra
    public static final String[] METHOD_RUNNERS = {};

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final MiddlewareList middlewareList;

    public MiddlewareInterceptor(MiddlewareList middlewareList) {
        this.middlewareList = middlewareList;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String requestUri = request.getRequestURI();

        // Bỏ qua các URL nằm trong danh sách ngoại lệ
        for (String excludedUrl : EXCLUDED_URLS) {
            if (PATH_MATCHER.match(excludedUrl, requestUri)) {
                return true;
            }
        }

        // Kiểm tra các URL nằm trong nhóm cần kiểm tra
        for (String urlPattern : URL_PATTERNS) {
            if (PATH_MATCHER.match(urlPattern, requestUri)) {

                // Kiểm tra các phương thức nằm trong METHOD_RUNNERS
                if (METHOD_RUNNERS.length != 0) {
                    for (String methodName : METHOD_RUNNERS) {
                        try {
                            Method method = middlewareList.getClass().getMethod(methodName);
                            Object result = method.invoke(middlewareList);
                            if (result instanceof String errorMessage && errorMessage != null) {
                                throw new MiddlewareException(errorMessage);
                            }
                        } catch (NoSuchMethodException e) {
                            throw new MiddlewareException("Not found middleware runner: " + methodName);
                        } catch (Exception e) {
                            throw new MiddlewareException(e.getMessage());
                        }
                    }
                }

                // Kiểm tra các phương thức nằm trong @Middleware
                if (handler instanceof HandlerMethod handlerMethod) {
                    Method method = handlerMethod.getMethod();
                    Middleware middleware = method.getAnnotation(Middleware.class);

                    if (middleware != null) {
                        for (String middlewareName : middleware.value()) {
                            try {
                                Method middlewareMethod = middlewareList.getClass().getMethod(middlewareName);
                                Object result = middlewareMethod.invoke(middlewareList);
                                if (result instanceof String errorMessage && errorMessage != null) {
                                    throw new MiddlewareException(errorMessage);
                                }
                            } catch (NoSuchMethodException e) {
                                throw new MiddlewareException("Not found middleware: " + middlewareName);
                            } catch (Exception e) {
                                throw new MiddlewareException(e.getMessage());
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
