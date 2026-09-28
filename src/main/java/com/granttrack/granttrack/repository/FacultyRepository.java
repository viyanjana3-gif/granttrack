package com.granttrack.granttrack.repository;

import com.granttrack.granttrack.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
}