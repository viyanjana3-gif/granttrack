package com.granttrack.granttrack.config;

import com.granttrack.granttrack.dto.AuthUser;
import com.granttrack.granttrack.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (isPublicResource(path)) {
            return true;
        }

        AuthUser user = authService.currentUser(request.getSession(false));
        if (user == null) {
            if (path.startsWith("/api/")) {
                return reject(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Login required");
            }
            response.sendRedirect("/login.html");
            return false;
        }

        request.setAttribute(AuthService.USER_ATTRIBUTE, user);
        if (isAdminOnlyApi(path, request.getMethod()) &&
                !AuthService.ADMIN.equals(user.role())) {
            return reject(response, HttpServletResponse.SC_FORBIDDEN,
                    "Administrator access required");
        }

        if (isAdminOnlyPage(path) && !AuthService.ADMIN.equals(user.role())) {
            response.sendRedirect("/applications.html");
            return false;
        }

        if (isFacultyOnlyPage(path) && AuthService.ADMIN.equals(user.role())) {
            response.sendRedirect("/applications.html");
            return false;
        }

        if ("/login.html".equals(path)) {
            response.sendRedirect(AuthService.ADMIN.equals(user.role())
                    ? "/index.html"
                    : "/applications.html");
            return false;
        }
        return true;
    }

    private boolean isPublicResource(String path) {
        return "/login.html".equals(path) ||
                "/style.css".equals(path) ||
                "/auth.js".equals(path) ||
                "/script.js".equals(path) ||
                "/api/auth/login".equals(path) ||
                "/api/auth/logout".equals(path) ||
                path.startsWith("/favicon");
    }

    private boolean isAdminOnlyApi(String path, String method) {
        // Faculty can PUT /api/faculty/{id} to update their own profile
        if (path.matches("/api/faculty/\\d+") && "PUT".equalsIgnoreCase(method)) {
            return false;
        }
        return path.startsWith("/api/dashboard") ||
                path.startsWith("/api/faculty") ||
                path.startsWith("/api/stages") ||
                (path.startsWith("/api/applications") &&
                        ("PUT".equalsIgnoreCase(method) ||
                         "DELETE".equalsIgnoreCase(method)));
    }

    private boolean isAdminOnlyPage(String path) {
        return "/".equals(path) ||
                "/index.html".equals(path) ||
                "/faculty.html".equals(path) ||
                "/stages.html".equals(path);
    }

    private boolean isFacultyOnlyPage(String path) {
        return "/new-application.html".equals(path) ||
                "/expenditures.html".equals(path);
    }

    private boolean reject(
            HttpServletResponse response,
            int status,
            String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
        return false;
    }
}