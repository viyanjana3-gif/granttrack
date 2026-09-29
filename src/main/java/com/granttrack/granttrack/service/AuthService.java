package com.granttrack.granttrack.service;

import com.granttrack.granttrack.dto.AuthUser;
import com.granttrack.granttrack.dto.LoginRequest;
import com.granttrack.granttrack.entity.Faculty;
import com.granttrack.granttrack.repository.FacultyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class AuthService {

    public static final String USER_ATTRIBUTE = "grantTrackUser";
    public static final String ADMIN = "ADMIN";
    public static final String FACULTY = "FACULTY";

    private final FacultyRepository facultyRepository;
    private final String adminUsername;
    private final String adminPassword;

    public AuthService(
            FacultyRepository facultyRepository,
            @Value("${granttrack.admin.username:admin}") String adminUsername,
            @Value("${granttrack.admin.password:}") String adminPassword) {
        this.facultyRepository = facultyRepository;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Transactional
    public AuthUser login(LoginRequest request, HttpServletRequest servletRequest) {
        if (ADMIN.equalsIgnoreCase(request.getRole())) {
            if (!validAdminCredentials(request)) {
                return null;
            }

            AuthUser user = new AuthUser(ADMIN, adminUsername, null);
            establishSession(servletRequest, user);
            return user;
        }

        if (!FACULTY.equalsIgnoreCase(request.getRole()) ||
                request.getName() == null || request.getName().isBlank()) {
            return null;
        }

        String name = request.getName().trim().replaceAll("\\s+", " ");
        Faculty faculty = facultyRepository.findFirstByNameIgnoreCase(name)
            .orElseGet(() -> facultyRepository.save(new Faculty(
                name,
                "Not provided",
                "not-provided@invalid.invalid")));

        AuthUser user = new AuthUser(FACULTY, faculty.getName(), faculty.getId());
        establishSession(servletRequest, user);
        return user;
    }

    public AuthUser currentUser(HttpSession session) {
        Object user = session == null ? null : session.getAttribute(USER_ATTRIBUTE);
        return user instanceof AuthUser authUser ? authUser : null;
    }

    private boolean validAdminCredentials(LoginRequest request) {
        if (adminPassword == null || adminPassword.isBlank() ||
                request.getUsername() == null || request.getPassword() == null) {
            return false;
        }

        byte[] expectedUsername = adminUsername.getBytes(StandardCharsets.UTF_8);
        byte[] providedUsername = request.getUsername().getBytes(StandardCharsets.UTF_8);
        byte[] expectedPassword = adminPassword.getBytes(StandardCharsets.UTF_8);
        byte[] providedPassword = request.getPassword().getBytes(StandardCharsets.UTF_8);

        return MessageDigest.isEqual(expectedUsername, providedUsername) &&
                MessageDigest.isEqual(expectedPassword, providedPassword);
    }

    private void establishSession(HttpServletRequest request, AuthUser user) {
        HttpSession session = request.getSession(true);
        request.changeSessionId();
        session.setAttribute(USER_ATTRIBUTE, user);
    }
}