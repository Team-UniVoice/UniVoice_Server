package sopt.univoice.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.auth.dto.CheckEmailRequest;
import sopt.univoice.domain.user.entity.Member;

import java.util.List;
import java.util.Optional;

public interface AuthRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);

    List<Member> findByUniversityName(String universityName);
}
