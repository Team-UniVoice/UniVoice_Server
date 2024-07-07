package sopt.univoice.domain.universityData.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.universityData.entity.Department;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByUniversityId(Long universityId);
}
