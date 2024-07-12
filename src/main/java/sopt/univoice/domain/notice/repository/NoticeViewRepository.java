package sopt.univoice.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.notice.entity.NoticeView;

public interface NoticeViewRepository extends JpaRepository<NoticeView, Long> {
}

