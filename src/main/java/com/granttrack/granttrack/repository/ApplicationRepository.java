package com.granttrack.granttrack.repository;

import com.granttrack.granttrack.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

	List<Application> findByFaculty_Id(Long facultyId);
}