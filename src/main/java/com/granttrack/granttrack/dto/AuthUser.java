package com.granttrack.granttrack.dto;

import java.io.Serializable;

public record AuthUser(String role, String name, Long facultyId)
        implements Serializable {

        public boolean isAdmin() {
                return "ADMIN".equals(role);
        }

        public boolean isFaculty() {
                return "FACULTY".equals(role);
        }
}