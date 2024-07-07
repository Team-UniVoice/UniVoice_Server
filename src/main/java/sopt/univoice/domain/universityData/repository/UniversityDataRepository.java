package sopt.univoice.domain.universityData.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.universityData.entity.University;

import java.util.Optional;


public interface UniversityDataRepository extends JpaRepository<University, Long> {
    Optional<University> findByUniversityName(String universityName);
}