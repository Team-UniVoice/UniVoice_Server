package sopt.univoice.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.notice.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
