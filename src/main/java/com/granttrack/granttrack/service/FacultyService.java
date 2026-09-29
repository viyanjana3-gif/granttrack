package com.granttrack.granttrack.service;

import com.granttrack.granttrack.dto.AuthUser;
import com.granttrack.granttrack.entity.Faculty;
import com.granttrack.granttrack.repository.FacultyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }

    public Faculty getFacultyById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));
    }

    public Faculty updateFaculty(Long id, Faculty updated, AuthUser user) {
        // Faculty can only update their own record; admin can update any
        if (user.isFaculty() && !id.equals(user.facultyId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only update your own profile");
        }
        Faculty existing = getFacultyById(id);
        if (updated.getDepartment() != null && !updated.getDepartment().isBlank()) {
            existing.setDepartment(updated.getDepartment());
        }
        if (updated.getEmail() != null && !updated.getEmail().isBlank()) {
            existing.setEmail(updated.getEmail());
        }
        return facultyRepository.save(existing);
    }

    public void deleteFaculty(Long id) {
        facultyRepository.deleteById(id);
    }
}