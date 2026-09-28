package com.granttrack.granttrack.repository;

import com.granttrack.granttrack.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageRepository extends JpaRepository<Stage, Long> {
}