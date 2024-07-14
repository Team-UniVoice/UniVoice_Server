package sopt.univoice.domain.affiliation.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.affiliation.entity.Affiliation;

public interface AffiliationRepository extends JpaRepository<Affiliation, Long> {
    Affiliation findByAffiliationAndAffiliationUniversityName(String affiliation, String affiliationUniversityName);
}