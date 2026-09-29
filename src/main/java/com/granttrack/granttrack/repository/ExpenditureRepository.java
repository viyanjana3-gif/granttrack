package com.granttrack.granttrack.repository;

import com.granttrack.granttrack.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    List<Expenditure> findByApplicationId(Long applicationId);

    List<Expenditure> findByApplication_StatusIgnoreCase(String status);
}