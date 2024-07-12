package sopt.univoice.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.universityData.entity.CollegeDepartment;



public interface CollegeDepartmentRepository extends JpaRepository<CollegeDepartment, Long> {
    // JpaRepository already provides findById method, no need to redefine it
}

