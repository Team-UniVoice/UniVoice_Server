package sopt.univoice.domain.universityData.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.universityData.entity.University;

public interface UniversityDataRepository extends JpaRepository<University, Long> {
}
