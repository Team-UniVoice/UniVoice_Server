package sopt.univoice.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.auth.dto.CheckEmailRequest;
import sopt.univoice.domain.user.entity.Member;

public interface AuthRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
}
