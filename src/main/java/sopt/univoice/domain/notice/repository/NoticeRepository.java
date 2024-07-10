package sopt.univoice.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.infra.common.exception.NotFoundException;
import sopt.univoice.infra.common.exception.message.ErrorMessage;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    default Notice findByIdOrThrow(Long id) {
        return this.findById(id)
                   .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_NOTICE));
    }
}
