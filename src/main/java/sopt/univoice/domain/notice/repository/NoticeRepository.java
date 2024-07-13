package sopt.univoice.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sopt.univoice.domain.notice.entity.Notice;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query("SELECT n FROM Notice n WHERE n.member.universityName = :universityName")
    List<Notice> findAllByMemberUniversityName(String universityName);

    List<Notice> findByMemberUniversityNameAndMemberAffiliationAffiliation(String universityName, String affiliation);


    @Query("SELECT n FROM Notice n WHERE n.member.universityName = :universityName AND n.member.affiliation.affiliation = :affiliation")
    List<Notice> findByMemberUniversityNameAndAffiliationAffiliation(String universityName, String affiliation);
}


