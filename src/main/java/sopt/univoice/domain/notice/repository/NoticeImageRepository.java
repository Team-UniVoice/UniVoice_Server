package sopt.univoice.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.notice.entity.NoticeImage;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, Long> {
}

