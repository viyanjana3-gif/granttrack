package com.granttrack.granttrack.repository;

import com.granttrack.granttrack.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}