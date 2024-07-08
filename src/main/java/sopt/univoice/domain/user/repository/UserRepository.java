package sopt.univoice.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.user.entity.User;
import sopt.univoice.infra.common.exception.NotFoundException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;

public interface UserRepository extends JpaRepository<User, Long> {

    default User findByIdOrThrow(Long idx) {
        return this.findById(idx)
                   .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_USER));
    }
}
