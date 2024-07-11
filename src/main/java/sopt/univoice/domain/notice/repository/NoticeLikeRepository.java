package sopt.univoice.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.univoice.domain.notice.entity.Notice;
import sopt.univoice.domain.notice.entity.NoticeLike;
import sopt.univoice.domain.user.entity.Member;

import java.util.Optional;

public interface NoticeLikeRepository extends JpaRepository<NoticeLike, Long> {
        boolean existsByNoticeAndMember(Notice notice, Member member);

        Optional<NoticeLike> findByNoticeAndMember(Notice notice, Member member);
}
