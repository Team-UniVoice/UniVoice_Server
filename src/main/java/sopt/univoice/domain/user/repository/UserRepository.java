package sopt.univoice.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.user.entity.Member;
import sopt.univoice.infra.common.exception.NotFoundException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;

public interface UserRepository extends JpaRepository<Member, Long> {

    default Member findByIdOrThrow(Long id) {
        return this.findById(id)
                   .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_USER));
    }
}
