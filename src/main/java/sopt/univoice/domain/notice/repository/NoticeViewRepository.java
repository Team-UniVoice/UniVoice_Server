package sopt.univoice.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.domain.notice.entity.NoticeView;
import sopt.univoice.domain.user.entity.Member;

import java.util.List;
import java.util.Optional;

public interface NoticeViewRepository extends JpaRepository<NoticeView, Long> {
    Optional<NoticeView> findByNoticeAndMember(Notice notice, Member member);
    List<NoticeView> findByMemberIdAndReadAtFalse(Long memberId);
}


