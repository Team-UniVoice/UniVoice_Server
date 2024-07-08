package sopt.univoice.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.domain.user.entity.User;
import sopt.univoice.infra.common.exception.NotFoundException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    default Notice findByIdOrThrow(Long idx) {
        return this.findById(idx)
                   .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_NOTICE));
    }
}
